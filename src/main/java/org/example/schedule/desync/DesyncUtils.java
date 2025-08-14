package org.example.schedule.desync;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilities for computing de-synchronization delays.
 *
 * <p>The delay is composed of:
 *
 * <ul>
 *   <li>Stable per-instance splay in {@code [0, window]}.
 *   <li>Random half-window component in {@code [0, window/2]}.
 *   <li>Per-run jitter in {@code [-jitter, +jitter]}.
 * </ul>
 *
 * The sum is clamped into {@code [0, window]} and returned as a {@link Duration}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DesyncUtils {

  /**
   * Compute a per-run delay for de-synchronizing a scheduled job.
   *
   * @param key unique job identifier
   * @param appName application name for instance identity
   * @param host host name for instance identity
   * @param window max additional delay after base time (must be > 0)
   * @param jitter max absolute jitter per run (must be ≥ 0)
   * @return delay duration in {@code [0, window]}
   */
  public static Duration computeDelay(
      String key, String appName, String host, Duration window, Duration jitter) {
    long winMs = Math.max(1L, window.toMillis()); // require positive window

    // Stable per-instance offset
    long splayMs = Math.floorMod(Objects.hash(appName, host, key), winMs);

    // Per-run random half-window
    long randMs = ThreadLocalRandom.current().nextLong(0, winMs + 1);

    // Per-run jitter
    long jitBound = Math.max(0L, jitter.toMillis());
    long jitMs =
        jitBound == 0L ? 0L : ThreadLocalRandom.current().nextLong(-jitBound, jitBound + 1);

    long totalMs = clamp(0, winMs, splayMs + randMs / 2 + jitMs);
    return Duration.ofMillis(totalMs);
  }

  /** Clamp {@code v} into [lo, hi]. */
  public static long clamp(long lo, long hi, long v) {
    return Math.max(lo, Math.min(hi, v));
  }
}
