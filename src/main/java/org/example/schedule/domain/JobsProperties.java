package org.example.schedule.domain;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class JobsProperties {
  private Map<String, JobConfig> jobs;
}
