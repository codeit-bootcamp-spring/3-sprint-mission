package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class ReadStatusException extends DiscodeitException {

  protected ReadStatusException(ErrorCode errorCode, String reason) {
    super(errorCode, reason);
  }
}
