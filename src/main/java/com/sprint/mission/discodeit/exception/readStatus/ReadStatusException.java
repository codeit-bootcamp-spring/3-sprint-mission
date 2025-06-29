package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.dto.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.time.Instant;
import java.util.Map;

public class ReadStatusException extends DiscodeitException {
  public ReadStatusException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
    super(timestamp, errorCode, details);
  }
}