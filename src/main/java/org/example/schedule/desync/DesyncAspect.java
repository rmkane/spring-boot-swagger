package org.example.schedule.desync;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.schedule.logging.MdcUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Aspect that applies a small, per-instance delay to {@code @Scheduled} jobs to reduce synchronized
 * bursts across multiple application instances.
 *
 * <p><b>Why</b>: In horizontally scaled deployments, identical cron expressions cause all instances
 * to fire at the same wall-clock time (the "thundering herd"). This aspect introduces a
 * de-synchronization (splay + jitter) <i>inside</i> the scheduled method so different instances run
 * at slightly different moments.
 *
 * <p><b>How it works</b>:
 *
 * <ol>
 *   <li>Reads {@link Desync} parameters from the annotated method: a job {@code key}, a maximum
 *       {@code window} to delay within, and {@code jitter}.
 *   <li>Computes a delay via {@link DesyncUtils#computeDelay(String, String, String, Duration,
 *       Duration)}:
 *       <ul>
 *         <li>Stable, per-instance splay based on {@code key + appName + host}.
 *         <li>Per-run random component and small jitter to prevent re-alignment.
 *       </ul>
 *   <li>Sleeps for that delay and then proceeds with the scheduled method.
 *   <li>Wraps execution in MDC (e.g., {@code jobId}) so logs can be correlated.
 * </ol>
 *
 * <p><b>When to use</b>:
 *
 * <ul>
 *   <li>You want a drop-in fix without changing your scheduler wiring.
 *   <li>You have a thread-pooled scheduler (e.g., {@code ThreadPoolTaskScheduler}) so a short sleep
 *       won’t block subsequent jobs.
 * </ul>
 *
 * <p><b>When to prefer {@link DesyncTrigger}</b>:
 *
 * <ul>
 *   <li>If you want a <i>non-blocking</i> approach that shifts the scheduled fire time itself.
 *   <li>If you already have trigger-based scheduling and want to log at the cron boundary.
 * </ul>
 *
 * <p><b>Caveats</b>:
 *
 * <ul>
 *   <li><b>Do not “double-desync”</b> the same job (i.e., avoid using both this aspect and {@link
 *       DesyncTrigger} for one job) unless you intentionally want a larger delay.
 *   <li>Ensure your scheduler has multiple threads; otherwise this sleep blocks other jobs. (e.g.,
 *       configure {@code ThreadPoolTaskScheduler#setPoolSize}).
 *   <li>{@code window} and {@code jitter} are parsed as ISO-8601 durations (e.g., {@code PT7M},
 *       {@code PT20S}).
 * </ul>
 *
 * <p><b>Logging</b>: With a Log4j2 pattern that includes MDC (e.g., {@code [jobId=%X{jobId}]}),
 * you’ll see the job ID on lines emitted during the job’s run. The aspect logs the applied delay at
 * DEBUG and the job duration at TRACE.
 *
 * <h3>Example</h3>
 *
 * <pre>{@code
 * @Desync(key = "jobA", window = "PT7M", jitter = "PT20S")
 * @Scheduled(cron = "0 0 * * * *", zone = "UTC")
 * public void jobA() { ... }
 * }</pre>
 */
@Slf4j
@Aspect
@Component
public class DesyncAspect {

  @Value("${spring.application.name:app}")
  private String appName;

  @Value("${HOSTNAME:unknown}")
  private String host;

  /**
   * Applies the computed desync delay, then invokes the scheduled method.
   *
   * @param pjp join point for the scheduled method
   * @param desync annotation providing key/window/jitter
   * @return the original method's return value (if any)
   * @throws Throwable rethrows any exception from the target method
   */
  @Around("@annotation(desync)")
  public Object around(ProceedingJoinPoint pjp, Desync desync) throws Throwable {
    final String key = desync.key();
    final Duration window = Duration.parse(desync.window());
    final Duration jitter = Duration.parse(desync.jitter());

    // Add minimal MDC to correlate logs from this job run.
    final Map<String, String> mdc = Map.of("jobId", key);

    return MdcUtils.withMdc(
        mdc,
        () -> {
          // Compute and apply delay.
          final Duration delay = DesyncUtils.computeDelay(key, appName, host, window, jitter);
          log.debug(
              "desync aspect: applying delay={} (window={} jitter={})", delay, window, jitter);

          sleep(delay);

          // Execute and record duration.
          final Instant start = Instant.now();
          try {
            return pjp.proceed();
          } finally {
            log.trace("desync aspect: job finished in {}", Duration.between(start, Instant.now()));
          }
        });
  }

  /**
   * Sleep helper that honors interrupts and ignores null/zero/negative durations.
   *
   * @param d delay duration; ignored if {@code null}, zero, or negative
   */
  private static void sleep(Duration d) {
    if (d == null || d.isZero() || d.isNegative()) return;
    try {
      Thread.sleep(d.toMillis());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }
}
