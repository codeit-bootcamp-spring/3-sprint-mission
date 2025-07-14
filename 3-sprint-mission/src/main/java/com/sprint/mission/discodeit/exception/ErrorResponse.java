package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  public ErrorResponse(DiscodeitException ex, int status) {
    this(Instant.now(), ex.getErrorCode().name(), ex.getMessage(), ex.getDetails(),
        ex.getClass().getSimpleName(), status);
  }

  public ErrorResponse(Exception ex, int status) {
    this(Instant.now(), ex.getClass().getSimpleName(), ex.getMessage(), new HashMap<>(),
        ex.getClass().getSimpleName(), status);
  }

}
