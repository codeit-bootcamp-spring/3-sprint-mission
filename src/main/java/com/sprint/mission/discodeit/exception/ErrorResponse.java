package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    String errorCode,
    String message,
    String exceptionType,
    int status,
    Instant timestamp,
    Map<String, String> details
) {

  public static ErrorResponse from(DiscodeitException ex) {
    return new ErrorResponse(
        ex.getErrorCode().name(),
        ex.getErrorCode().getMessage(),
        ex.getClass().getSimpleName(),
        ex.getErrorCode().getStatus(),
        Instant.now(),
        ex.getDetails()
    );
  }
}
