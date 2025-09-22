package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class NotValidTokenException extends DiscodeitException {

  public NotValidTokenException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}
