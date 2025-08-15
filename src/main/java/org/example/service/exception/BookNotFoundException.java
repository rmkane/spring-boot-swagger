package org.example.service.exception;

import java.io.Serial;

public class BookNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = -7785085924007243253L;

  public BookNotFoundException() {
    super();
  }

  public BookNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public BookNotFoundException(final String message) {
    super(message);
  }

  public BookNotFoundException(final Throwable cause) {
    super(cause);
  }
}
