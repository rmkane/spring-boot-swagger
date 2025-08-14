package org.example.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.schedule.domain.JobConfig;

@RequiredArgsConstructor
@Slf4j
public class JobHandler implements Runnable {
  private final String jobId;
  private final JobConfig cfg;

  @Override
  public void run() {
    log.debug("Executing job {} ({})", jobId, cfg.getDescription());

    log.info("Making request to ({}): - {}", jobId, cfg.getEndpoint());
  }
}
