package org.example.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.service.exception.BookNotFoundException;
import org.example.web.util.ErrorResponseUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BookNotFoundException.class)
  public Object handleBookNotFound(
      BookNotFoundException ex, HttpServletRequest request, Model model) {
    String requestPath = request.getRequestURI();

    // Check if this is an API request
    if (requestPath.contains("/api/")) {
      return ErrorResponseUtil.createApiErrorResponse(404, "Book not found", requestPath);
    }

    // Return error page for web requests
    model.addAttribute("status", "404");
    model.addAttribute("error", "Book Not Found");
    model.addAttribute("message", ex.getMessage());
    return "error";
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public Object handleNoHandlerFound(
      NoHandlerFoundException ex, HttpServletRequest request, Model model)
      throws NoHandlerFoundException {
    String requestPath = request.getRequestURI();

    // Skip handling for static resource requests (let Spring handle them normally)
    if (isStaticResourceRequest(requestPath)) {
      throw ex; // Let Spring handle static resource 404s normally
    }

    // Check if this is an API request
    if (requestPath.contains("/api/")) {
      return ErrorResponseUtil.createApiErrorResponse(404, "Resource not found", requestPath);
    }

    // Return error page for web requests
    model.addAttribute("status", "404");
    model.addAttribute("error", "Not Found");
    model.addAttribute("message", "The requested page '" + requestPath + "' could not be found.");
    return "error";
  }

  @ExceptionHandler(Exception.class)
  public Object handleGenericException(Exception ex, HttpServletRequest request, Model model) {
    String requestPath = request.getRequestURI();

    // Check if this is an API request
    if (requestPath.contains("/api/")) {
      return ErrorResponseUtil.createApiErrorResponse(500, "Internal server error", requestPath);
    }

    // Return error page for web requests
    model.addAttribute("status", "500");
    model.addAttribute("error", "Internal Server Error");
    model.addAttribute("message", "An unexpected error occurred.");
    return "error";
  }

  /** Check if the request is for a static resource */
  private boolean isStaticResourceRequest(String requestPath) {
    return requestPath.contains(".")
        && (requestPath.endsWith(".css")
            || requestPath.endsWith(".js")
            || requestPath.endsWith(".png")
            || requestPath.endsWith(".jpg")
            || requestPath.endsWith(".jpeg")
            || requestPath.endsWith(".gif")
            || requestPath.endsWith(".ico")
            || requestPath.endsWith(".svg")
            || requestPath.endsWith(".woff")
            || requestPath.endsWith(".woff2")
            || requestPath.endsWith(".ttf")
            || requestPath.endsWith(".eot"));
  }
}
