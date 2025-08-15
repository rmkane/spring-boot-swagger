package org.example.service.exception;

import java.io.Serial;

public class BookIdMismatchException extends RuntimeException {
  @Serial private static final long serialVersionUID = -3456433964796118325L;

  public BookIdMismatchException() {
    super();
  }

  public BookIdMismatchException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public BookIdMismatchException(final String message) {
    super(message);
  }

  public BookIdMismatchException(final Throwable cause) {
    super(cause);
  }
}
