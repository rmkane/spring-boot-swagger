package org.example.schedule.desync;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

/**
 * Trigger wrapper that shifts each execution forward by a de-synchronization delay.
 *
 * <p>Useful for wrapping a {@link CronTrigger} (or any {@link Trigger}) to avoid simultaneous fires
 * across independent instances.
 */
@RequiredArgsConstructor
@Slf4j
public final class DesyncTrigger implements InitializingBean, Trigger {
  private final Trigger delegate;
  private final TaskScheduler prelogScheduler;
  private final String key, appName, host;
  private final Duration window, jitter;

  // These defaults correspond to the ones defined in @Desync
  private static final Duration DEFAULT_WINDOW = Duration.parse("PT7M");
  private static final Duration DEFAULT_JITTER = Duration.parse("PT20S");
  private static final Duration MIN_FALLBACK_SKEW = Duration.ofMillis(250);

  @Override
  public void afterPropertiesSet() {
    requireNonNull(delegate, "delegate");
    requireNonNull(key, "key");
    requireNonNull(appName, "appName");
    requireNonNull(host, "host");
    requirePositive(window, "window");
    requireNonNegative(jitter, "jitter");
  }

  @Override
  public Instant nextExecution(@NonNull TriggerContext ctx) {
    final Instant base = delegate.nextExecution(ctx);
    if (base == null) return null;

    final Instant now = Instant.now();
    final Duration delay = DesyncUtils.computeDelay(key, appName, host, window, jitter);
    final Instant shifted = computeShift(base, now, delay);

    // Log once at the cron boundary (only if that boundary is still in the future)
    if (prelogScheduler != null && base.isAfter(now)) {
      prelogScheduler.schedule(
          () ->
              log.debug(
                  "desync plan key={} base={} delay={} -> runAt={}", key, base, delay, shifted),
          base);
    }

    log.trace("desync computed key={} base={} delay={} -> runAt={}", key, base, delay, shifted);

    return shifted;
  }

  private Instant computeShift(Instant base, Instant now, Duration delay) {
    // Preferred time: cron base + computed delay
    Instant candidate = base.plus(delay);

    // If the candidate is in the future, use it as-is.
    if (candidate.isAfter(now)) return candidate;

    // If we’re late (e.g., scheduled inside the window), push to now + a minimal skew
    Duration minSkew = delay.isZero() ? MIN_FALLBACK_SKEW : delay;
    return now.plus(minSkew);
  }

  private static Duration requirePositive(Duration d, String name) {
    requireNonNull(d, name);
    if (d.isZero() || d.isNegative()) {
      throw new IllegalArgumentException(name + " must be > PT0S");
    }
    return d;
  }

  private static Duration requireNonNegative(Duration d, String name) {
    requireNonNull(d, name);
    if (d.isNegative()) {
      throw new IllegalArgumentException(name + " must be ≥ PT0S");
    }
    return d;
  }

  /** Wrap an existing trigger. */
  public static Trigger wrap(
      Trigger delegate,
      TaskScheduler prelogScheduler,
      String key,
      String appName,
      String host,
      Duration window,
      Duration jitter) {
    return new DesyncTrigger(delegate, prelogScheduler, key, appName, host, window, jitter);
  }

  /** Wrap an existing trigger with default window of 7 minutes with 20 seconds of jitter. */
  public static Trigger wrap(
      Trigger delegate, TaskScheduler prelogScheduler, String key, String appName, String host) {
    return wrap(delegate, prelogScheduler, key, appName, host, DEFAULT_WINDOW, DEFAULT_JITTER);
  }
}
