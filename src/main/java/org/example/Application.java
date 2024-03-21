package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@EnableJpaRepositories("org.example.persistence.repo")
@EntityScan("org.example.persistence.model")
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);

    log.debug("Debug log message");
    log.info("Info log message");
    log.error("Error log message");
    log.warn("Warn log message");
    log.trace("Trace log message");
  }
}
