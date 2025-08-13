package org.example.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LegacyJobRunner {
  @Scheduled(cron = "0 * * * * *")
  void cron() {
    log.info("Running legacy job (every minute)...");
  }

  @Scheduled(fixedRateString = "PT10S")
  void rate10() {
    log.info("Running legacy job (10s)...");
  }

  @Scheduled(fixedRateString = "PT20S")
  void rate20() {
    log.info("Running legacy job (20s)...");
  }

  @Scheduled(fixedRateString = "PT30S")
  void rate30() {
    log.info("Running legacy job (30s)...");
  }
}
