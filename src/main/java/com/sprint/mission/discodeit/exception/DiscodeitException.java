package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp = Instant.now();
  private final ErrorCode errorCode;
  private final Map<String, String> details;

  public DiscodeitException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = Collections.emptyMap();
  }

  public DiscodeitException(ErrorCode errorCode, String reason) {
    this(errorCode, Map.of("reason", reason));
  }

  public DiscodeitException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details;
  }

  public DiscodeitException(ErrorCode errorCode, Map<String, String> details, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
    this.details = details;
  }
}
