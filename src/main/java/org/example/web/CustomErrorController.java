package org.example.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public Object handleError(HttpServletRequest request, Model model) {
    System.out.println("=== CUSTOM ERROR CONTROLLER CALLED ===");
    System.out.println("Request URI: " + request.getRequestURI());
    System.out.println("Request URL: " + request.getRequestURL());
    System.out.println("All attributes:");

    // Print all request attributes for debugging
    java.util.Enumeration<String> attributeNames = request.getAttributeNames();
    while (attributeNames.hasMoreElements()) {
      String name = attributeNames.nextElement();
      Object value = request.getAttribute(name);
      System.out.println("  " + name + " = " + value);
    }

    // Try both old and new attribute names for compatibility
    Object status = request.getAttribute("javax.servlet.error.status_code");
    if (status == null) {
      status = request.getAttribute("jakarta.servlet.error.status_code");
    }

    Object message = request.getAttribute("javax.servlet.error.message");
    if (message == null) {
      message = request.getAttribute("jakarta.servlet.error.message");
    }

    Object path = request.getAttribute("javax.servlet.error.request_uri");
    if (path == null) {
      path = request.getAttribute("jakarta.servlet.error.request_uri");
    }

    int statusCode = status != null ? (Integer) status : 500;
    String errorMessage = message != null ? message.toString() : "An error occurred";
    String requestPath = path != null ? path.toString() : request.getRequestURI();

    System.out.println(
        "Final values - Status: "
            + statusCode
            + ", Path: "
            + requestPath
            + ", Message: "
            + errorMessage);

    // Check if this is an API request
    if (requestPath.contains("/api/")) {
      // Return JSON for API requests
      return createApiErrorResponse(statusCode, errorMessage, requestPath);
    }

    // Return error page for web requests
    if (statusCode == 404) {
      model.addAttribute("status", "404");
      model.addAttribute("error", "Not Found");
      model.addAttribute("message", "The requested page '" + requestPath + "' could not be found.");
    } else {
      model.addAttribute("status", String.valueOf(statusCode));
      model.addAttribute("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
      model.addAttribute("message", errorMessage);
    }

    System.out.println("Returning error page");
    return "error";
  }

  private ResponseEntity<Map<String, Object>> createApiErrorResponse(
      int statusCode, String message, String path) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
    errorResponse.put("message", message);
    errorResponse.put("status", statusCode);
    errorResponse.put("path", path);

    return ResponseEntity.status(HttpStatus.valueOf(statusCode))
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorResponse);
  }
}
