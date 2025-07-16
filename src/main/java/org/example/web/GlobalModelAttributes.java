package org.example.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

  @ModelAttribute("requestUri")
  public String requestUri(HttpServletRequest request) {
    return request.getRequestURI();
  }
}
