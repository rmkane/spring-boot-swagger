package org.example.schedule.domain;

import lombok.Data;

@Data
public class JobConfig {
  private JobType type;
  private String value;
  private String endpoint;
  private String description;
  private boolean disabled;
}
