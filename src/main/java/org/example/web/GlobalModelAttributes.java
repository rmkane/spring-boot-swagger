package org.example.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

  @Value("${spring.h2.console.path:/h2}")
  private String h2ConsolePath;

  @Value("${spring.application.name}")
  private String appName;

  @ModelAttribute("requestUri")
  public String requestUri(HttpServletRequest request) {
    return request.getRequestURI();
  }

  @ModelAttribute("swaggerUiPath")
  public String swaggerUiPath() {
    return "/swagger-ui/index.html";
  }

  @ModelAttribute("h2ConsolePath")
  public String h2ConsolePath() {
    return h2ConsolePath;
  }

  @ModelAttribute("appName")
  public String appName() {
    return appName;
  }
}
