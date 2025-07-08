package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Map<String, Object> details;
  private final Instant timestamp;

  public DiscodeitException(ErrorCode errorCode) {
    super(errorCode.getDefaultMessage());
    this.errorCode = errorCode;
    this.details = new HashMap<>();
    this.timestamp = Instant.now();
  }


  public DiscodeitException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.details = new HashMap<>();
    this.timestamp = Instant.now();
  }

  public DiscodeitException(ErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.details = new HashMap<>();
    this.timestamp = Instant.now();
  }

  public DiscodeitException addDetails(String key, Object value) {
    this.details.put(key, value);
    return this;
  }
}
