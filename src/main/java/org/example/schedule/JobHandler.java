package org.example.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedule.domain.JobConfig;

@Slf4j
@AllArgsConstructor
public class JobHandler implements Runnable {
  private final String jobId;
  private final JobConfig cfg;

  @Override
  public void run() {
    log.debug("Executing job {} ({})", jobId, cfg.getDescription());

    log.info("Making request to ({}): - {}", jobId, cfg.getEndpoint());
  }
}
