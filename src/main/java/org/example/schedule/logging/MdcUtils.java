package org.example.schedule.logging;

import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Utilities for running code with SLF4J {@link MDC} entries applied for the duration of the call.
 *
 * <p><strong>Why:</strong> In thread-pooled apps (schedulers, executors, web servers) it’s useful
 * to tag logs with contextual data (e.g., {@code jobId}, {@code tenant}, {@code requestId}). These
 * helpers set MDC keys before running the action and guarantee cleanup afterward, even on
 * exception. They work for both void and returning actions and provide a simple wrapper to decorate
 * {@link Runnable}s for schedulers.
 *
 * <h3>Examples</h3>
 *
 * <pre>{@code
 * // 1) Run a Runnable with MDC
 * MdcUtils.withMdc(Map.of("jobId", jobId), () -> doWork());
 *
 * // 2) Run a returning action with MDC
 * String result = MdcUtils.withMdc(Map.of("reqId", reqId), () -> service.call());
 *
 * // 3) Wrap a Runnable for scheduling
 * Runnable task = MdcUtils.wrap(baseTask, Map.of("jobId", jobId, "jobType", type));
 * taskScheduler.schedule(task, trigger);
 * }</pre>
 *
 * <p><strong>Notes:</strong>
 *
 * <ul>
 *   <li>Use a log pattern that renders MDC (e.g., Log4j2: {@code [jobId=%X{jobId}]} or {@code %X}).
 *   <li>{@code Map.of(...)} does not allow nulls. If a value may be null, use {@code
 *       String.valueOf(value)}.
 *   <li>These helpers set MDC on the <em>current thread</em>. If your action spins new threads,
 *       pass MDC explicitly to those threads as well.
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MdcUtils {

  /**
   * Functional interface like a Supplier that may throw any {@link Throwable}.
   *
   * @param <T> result type
   */
  @FunctionalInterface
  public interface ThrowingSupplier<T> {
    T get() throws Throwable;
  }

  /**
   * Run a {@link Runnable} with the given MDC values set, then automatically clear them. Any
   * exception thrown by the runnable is propagated (wrapped if thrown by MDC cleanup).
   *
   * @param values MDC key/value pairs to apply for the duration of {@code action}
   * @param action code to run with MDC applied
   * @throws RuntimeException if MDC cleanup unexpectedly throws
   */
  public static void withMdc(Map<String, String> values, Runnable action) {
    try (AutoCloseable ignored = closeableMdc(values)) {
      action.run();
    } catch (Exception e) { // MDC cleanup should not fail; wrap defensively if it does
      throw new RuntimeException(e);
    }
  }

  /**
   * Run a returning action with the given MDC values set, then automatically clear them. Any {@link
   * Throwable} from the action is propagated to the caller.
   *
   * @param values MDC key/value pairs to apply
   * @param action code to run that may return a value and throw
   * @param <T> result type
   * @return the value returned by {@code action}
   * @throws Throwable anything thrown by {@code action}
   */
  public static <T> T withMdc(Map<String, String> values, ThrowingSupplier<T> action)
      throws Throwable {
    try (AutoCloseable ignored = closeableMdc(values)) {
      return action.get();
    }
  }

  /**
   * Wrap a {@link Runnable} so that MDC values are applied during {@link Runnable#run()} and
   * automatically cleared afterward. Convenient for schedulers/executors that accept a Runnable.
   *
   * @param delegate the original runnable
   * @param values MDC key/value pairs to apply while running
   * @return a runnable that manages MDC around {@code delegate}
   */
  public static Runnable wrap(Runnable delegate, Map<String, String> values) {
    return () -> withMdc(values, delegate);
  }

  /**
   * Builds a single {@link AutoCloseable} that installs all MDC entries now and removes them (in
   * reverse order) when closed. Any exceptions during cleanup are suppressed.
   */
  private static AutoCloseable closeableMdc(Map<String, String> values) {
    final AutoCloseable[] closers =
        values.entrySet().stream()
            .map(e -> MDC.putCloseable(e.getKey(), e.getValue()))
            .toArray(AutoCloseable[]::new);

    return () -> {
      for (int i = closers.length - 1; i >= 0; i--) {
        try {
          closers[i].close();
        } catch (Exception ignore) {
          log.trace("Error closing MDC", ignore);
        }
      }
    };
  }
}
