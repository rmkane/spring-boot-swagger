package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupLogger implements ApplicationListener<ApplicationReadyEvent> {

  @Value("${server.port}")
  private int port;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    System.out.printf("%nApplication is running at http://localhost:%d%n%n", port);
  }
}
