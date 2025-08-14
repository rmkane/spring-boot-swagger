package org.example.schedule.desync;

import java.lang.annotation.*;

/**
 * Marks a scheduled method to be de-synchronized in a multi-instance deployment.
 *
 * <p>Without desync, identical cron expressions can cause all instances to fire at the same
 * wall-clock time (the "thundering herd" problem). This annotation signals that a {@link
 * org.example.schedule.desync.DesyncAspect} should apply a per-instance offset ("splay") plus small
 * per-run jitter before executing the method.
 *
 * <p>Each job's delay is computed from:
 *
 * <ul>
 *   <li>A deterministic per-instance splay derived from {@code key} + instance identity.
 *   <li>A per-run random component to reduce collisions.
 *   <li>Optional per-run jitter to prevent long-term re-alignment.
 * </ul>
 *
 * The total delay is clamped into the {@code [0, window]} range.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Desync {

  /**
   * Unique job identifier. Different keys will desync independently; using the same key will align
   * jobs.
   */
  String key();

  /**
   * Maximum delay window to apply after the base execution time. ISO-8601 duration string (e.g.,
   * "PT7M" = 7 minutes).
   */
  String window() default "PT7M";

  /**
   * Maximum absolute jitter to add/subtract each run. ISO-8601 duration string (e.g., "PT20S" = ±20
   * seconds).
   */
  String jitter() default "PT20S";
}
