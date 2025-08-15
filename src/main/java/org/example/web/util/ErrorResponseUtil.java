package org.example.web.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** Utility class for creating standardized error responses. */
public final class ErrorResponseUtil {

  private ErrorResponseUtil() {
    // Utility class - prevent instantiation
  }

  /**
   * Creates a standardized API error response.
   *
   * @param statusCode HTTP status code
   * @param message error message
   * @param path request path
   * @return ResponseEntity with standardized error format
   */
  public static ResponseEntity<Map<String, Object>> createApiErrorResponse(
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
