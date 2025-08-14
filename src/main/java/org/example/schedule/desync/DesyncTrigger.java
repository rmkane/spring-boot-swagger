package org.example.schedule.desync;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public final class DesyncTrigger implements Trigger {
  private final Trigger delegate;
  private final String key, appName, host;
  private final Duration window, jitter;

  // used only to log at base time (optional)
  private final TaskScheduler prelogScheduler;

  public DesyncTrigger(
      Trigger delegate,
      String key,
      String appName,
      String host,
      Duration window,
      Duration jitter,
      TaskScheduler prelogScheduler) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.key = Objects.requireNonNull(key, "key");
    this.appName = Objects.requireNonNull(appName, "appName");
    this.host = Objects.requireNonNull(host, "host");
    this.window = requirePositive(window, "window");
    this.jitter = requireNonNegative(jitter, "jitter");
    this.prelogScheduler = prelogScheduler;
  }

  @Override
  public Instant nextExecution(TriggerContext ctx) {
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
    Duration minSkew = delay.isZero() ? Duration.ofMillis(250) : delay;
    return now.plus(minSkew);
  }

  private static Duration requirePositive(Duration d, String name) {
    Objects.requireNonNull(d, name);
    if (d.isZero() || d.isNegative()) {
      throw new IllegalArgumentException(name + " must be > PT0S");
    }
    return d;
  }

  private static Duration requireNonNegative(Duration d, String name) {
    Objects.requireNonNull(d, name);
    if (d.isNegative()) {
      throw new IllegalArgumentException(name + " must be ≥ PT0S");
    }
    return d;
  }
}
