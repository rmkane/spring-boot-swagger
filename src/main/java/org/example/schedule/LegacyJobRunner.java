package org.example.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.schedule.desync.Desync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LegacyJobRunner {
  @Value("${app.legacy.jobs.cron1.disabled}")
  private boolean disabledCron1;

  @Value("${app.legacy.jobs.cron5.disabled}")
  private boolean disabledCron5;

  @Value("${app.legacy.jobs.rate10.disabled}")
  private boolean disabledRate10;

  @Value("${app.legacy.jobs.rate20.disabled}")
  private boolean disabledRate20;

  @Value("${app.legacy.jobs.rate30.disabled}")
  private boolean disabledRate30;

  @Desync(key = "cron1", window = "PT10s", jitter = "PT2S")
  @Scheduled(cron = "${app.legacy.jobs.cron1.value}", zone = "UTC")
  void cron1() {
    if (disabledCron1) {
      log.debug("cron1 is disabled");
      return;
    }

    log.info("Running legacy job (every minute)...");
  }

  @Desync(key = "cron5", window = "PT30S", jitter = "PT5S")
  @Scheduled(cron = "${app.legacy.jobs.cron5.value}", zone = "UTC")
  void cron5() {
    if (disabledCron5) {
      log.debug("cron5 is disabled");
      return;
    }

    log.info("Running legacy job (every 5th minute)...");
  }

  @Scheduled(fixedRateString = "${app.legacy.jobs.rate10.value}")
  void rate10() {
    if (disabledRate10) {
      log.debug("rate10 is disabled");
      return;
    }

    log.info("Running legacy job (10s)...");
  }

  @Scheduled(fixedRateString = "${app.legacy.jobs.rate20.value}")
  void rate20() {
    if (disabledRate20) {
      log.debug("rate20 is disabled");
      return;
    }

    log.info("Running legacy job (20s)...");
  }

  @Scheduled(fixedRateString = "${app.legacy.jobs.rate30.value}")
  void rate30() {
    if (disabledRate30) {
      log.debug("rate30 is disabled");
      return;
    }

    log.info("Running legacy job (30s)...");
  }
}
