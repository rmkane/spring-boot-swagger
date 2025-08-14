package org.example.schedule.logging;

import java.util.Map;
import org.slf4j.MDC;

/**
 * Runnable decorator that sets MDC entries for the duration of the task, then automatically clears
 * them using {@link MDC#putCloseable}.
 */
public final class MdcRunnable implements Runnable {

  private final Runnable delegate;
  private final Map<String, String> contextMap;

  public MdcRunnable(Runnable delegate, Map<String, String> contextMap) {
    this.delegate = delegate;
    this.contextMap = contextMap;
  }

  @Override
  public void run() {
    try (var closers = new Closers(contextMap)) {
      delegate.run();
    }
  }

  /** Helper to close all MDC contexts in reverse order. */
  private static final class Closers implements AutoCloseable {
    private final AutoCloseable[] closeables;

    Closers(Map<String, String> contextMap) {
      closeables =
          contextMap.entrySet().stream()
              .map(e -> MDC.putCloseable(e.getKey(), e.getValue()))
              .toArray(AutoCloseable[]::new);
    }

    @Override
    public void close() {
      for (int i = closeables.length - 1; i >= 0; i--) {
        try {
          closeables[i].close();
        } catch (Exception ignore) {
        }
      }
    }
  }
}
