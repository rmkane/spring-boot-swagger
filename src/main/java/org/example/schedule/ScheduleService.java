package org.example.schedule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedule.desync.DesyncTrigger;
import org.example.schedule.domain.JobConfig;
import org.example.schedule.domain.JobsProperties;
import org.example.schedule.logging.MdcRunnable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public final class ScheduleService {
  @Value("${spring.application.name}")
  private String appName;

  @Value("${HOSTNAME:unknown}")
  private String host;

  private final JobsProperties jobsProperties;
  private final TaskScheduler taskScheduler;

  // jobId -> Runnable to execute
  private final Map<String, Runnable> jobHandlers = new ConcurrentHashMap<>();
  // jobId -> ScheduledFuture handle
  private final Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

  /** Allow other components to register task bodies before startup. */
  public void register(String jobId, Runnable handler) {
    jobHandlers.put(Objects.requireNonNull(jobId), Objects.requireNonNull(handler));
  }

  /** Cancel a single job if present. */
  public boolean cancel(String jobId) {
    ScheduledFuture<?> prev = scheduled.remove(jobId);
    return prev != null && prev.cancel(false);
  }

  @PostConstruct
  public void start() {
    log.debug("Candidates for desync: { appName: {}, host: {} }", appName, host);

    Map<String, JobConfig> jobs = jobsProperties.getJobs();
    if (CollectionUtils.isEmpty(jobs)) {
      log.info("No jobs are scheduled");
      return;
    }

    jobs.forEach(
        (jobId, cfg) -> {
          try {
            if (cfg == null || cfg.isDisabled()) {
              log.info("Job is disabled or missing config: {}", jobId);
              return;
            }
            schedule(jobId, cfg);
          } catch (Exception e) {
            log.error("Failed to schedule job {}: {}", jobId, e.getMessage(), e);
          }
        });
  }

  @PreDestroy
  public void stop() {
    scheduled.forEach(
        (id, future) -> {
          future.cancel(false);
          log.info("Cancelled job {}", id);
        });
    scheduled.clear();
  }

  // ---------- Internals ----------

  private void schedule(String jobId, JobConfig cfg) {
    Objects.requireNonNull(cfg.getType(), "Job type is required");
    String rawValue = Objects.requireNonNull(cfg.getValue(), "Job value is required").trim();

    Runnable baseTask = jobHandlers.getOrDefault(jobId, new JobHandler(jobId, cfg));
    Runnable task =
        new MdcRunnable(baseTask, Map.of("jobId", jobId, "jobType", String.valueOf(cfg.getType())));

    switch (cfg.getType()) {
      case CRON -> {
        Trigger trigger = cronTrigger(jobId, rawValue);
        track(
            jobId,
            () -> taskScheduler.schedule(task, trigger),
            () -> String.format("Scheduled CRON job %s with '%s'", jobId, rawValue));
      }
      case DURATION -> {
        Duration period = parsePositiveDuration(rawValue);
        track(
            jobId,
            () -> taskScheduler.scheduleAtFixedRate(task, period),
            () -> String.format("Scheduled INTERVAL job %s every %s", jobId, period));
      }
      default -> throw new IllegalArgumentException("Unsupported job type: " + cfg.getType());
    }
  }

  /** Validate Spring 6-field cron and create a trigger. */
  private Trigger cronTrigger(String jobId, String expr) {
    try {
      // Validates and throws IllegalArgumentException if bad.
      CronExpression.parse(expr);
      // Each job could supply the window and jitter
      return new DesyncTrigger(
          new CronTrigger(expr, ZoneOffset.UTC),
          jobId,
          appName,
          host,
          Duration.parse("PT5S"),
          Duration.parse("PT1S"),
          taskScheduler);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(
          "Invalid CRON expression for job "
              + jobId
              + ": '"
              + expr
              + "'. Expected 6 fields: sec min hour day-of-month month day-of-week (optional year).",
          ex);
    }
  }

  /** Parse ISO-8601 duration and ensure it’s strictly positive. */
  private Duration parsePositiveDuration(String value) {
    try {
      Duration d = Duration.parse(value);
      if (d.isZero() || d.isNegative()) {
        throw new IllegalArgumentException("Duration must be positive: " + value);
      }
      return d;
    } catch (RuntimeException e) {
      throw new IllegalArgumentException(
          "Invalid ISO-8601 duration (e.g., PT30S, PT5M, PT1H, P1DT2H): " + value, e);
    }
  }

  /**
   * Schedule via supplier, cancel any existing, store handle, and log once. Keeps CRON and INTERVAL
   * branches DRY.
   */
  private void track(
      String jobId, Supplier<ScheduledFuture<?>> scheduleOp, Supplier<String> logMsg) {
    // Cancel any prior schedule for this jobId to keep behavior idempotent.
    ScheduledFuture<?> prev = scheduled.remove(jobId);
    if (prev != null) {
      prev.cancel(false);
      log.debug("Replaced existing schedule for {}", jobId);
    }

    ScheduledFuture<?> future = scheduleOp.get();
    if (future == null) {
      throw new IllegalStateException("TaskScheduler returned null ScheduledFuture for " + jobId);
    }

    scheduled.put(jobId, future);
    log.info(logMsg.get());
  }
}
