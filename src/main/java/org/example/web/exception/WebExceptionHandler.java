package org.example.web.exception;

import java.util.HashMap;
import java.util.Map;
import org.example.service.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(BookNotFoundException.class)
  public Object handleBookNotFound(BookNotFoundException ex, WebRequest request, Model model) {
    String requestUri = request.getDescription(false);

    if (requestUri.contains("/api/")) {
      return createApiErrorResponse(ex, requestUri, HttpStatus.NOT_FOUND);
    }

    return createWebErrorPage(ex, model, HttpStatus.NOT_FOUND);
  }

  /** Creates a standardized API error response */
  private ResponseEntity<Map<String, Object>> createApiErrorResponse(
      Exception ex, String requestUri, HttpStatus status) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", getErrorTitle(ex));
    errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : getDefaultMessage(ex));
    errorResponse.put("status", status.value());
    errorResponse.put("path", requestUri);

    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorResponse);
  }

  /** Creates a web error page with appropriate attributes */
  private String createWebErrorPage(Exception ex, Model model, HttpStatus status) {
    model.addAttribute("status", String.valueOf(status.value()));
    model.addAttribute("error", getWebErrorTitle(ex, status));
    model.addAttribute(
        "message", ex.getMessage() != null ? ex.getMessage() : getDefaultMessage(ex));
    return "error";
  }

  /** Gets the error title based on exception type */
  private String getErrorTitle(Exception ex) {
    if (ex instanceof BookNotFoundException) {
      return "Book not found";
    }
    return "Error";
  }

  /** Gets the web error title based on exception type and status */
  private String getWebErrorTitle(Exception ex, HttpStatus status) {
    if (ex instanceof BookNotFoundException) {
      return "Not Found";
    }
    return status.getReasonPhrase();
  }

  /** Gets the default message based on exception type */
  private String getDefaultMessage(Exception ex) {
    if (ex instanceof BookNotFoundException) {
      return "The requested book could not be found";
    }
    return "An unexpected error occurred";
  }
}
