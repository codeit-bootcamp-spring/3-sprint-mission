package com.sprint.mission.discodeit.dto.response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
    String error,
    String message,
    int status,
    LocalDateTime timestamp
) {

  public static ErrorResponse of(HttpStatus status, String message) {
    return new ErrorResponse(
        status.getReasonPhrase(),
        message,
        status.value(),
        LocalDateTime.now()
    );
  }
}
