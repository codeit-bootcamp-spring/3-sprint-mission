package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class ReadStatusException extends DiscodeitException {

  public ReadStatusException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected ReadStatusException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected ReadStatusException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
