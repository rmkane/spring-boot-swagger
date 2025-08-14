package org.example.schedule.desync;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Aspect that applies {@link DesyncUtils#computeDelay} before executing methods annotated with
 * {@link Desync}.
 *
 * <p>This works with Spring's {@link org.springframework.scheduling.annotation.Scheduled} to spread
 * out executions across instances. It sleeps inside the scheduler thread, so ensure your scheduler
 * has enough threads to avoid blocking unrelated jobs (e.g., {@link
 * org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}).
 */
@Component
@Aspect
@Slf4j
public class DesyncAspect {

  @Value("${spring.application.name:app}")
  private String appName;

  @Value("${HOSTNAME:unknown}")
  private String host;

  @Around("@annotation(desync)")
  public Object around(ProceedingJoinPoint pjp, Desync desync) throws Throwable {
    String key = desync.key();
    Duration window = Duration.parse(desync.window());
    Duration jitter = Duration.parse(desync.jitter());

    Duration delay = DesyncUtils.computeDelay(key, appName, host, window, jitter);
    log.debug("Adding delay to scheduled job {}: {}", key, delay);

    // Blocking sleep; safe if scheduler has multiple threads
    if (!delay.isZero() && !delay.isNegative()) {
      try {
        Thread.sleep(delay.toMillis());
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
    }

    return pjp.proceed();
  }
}
