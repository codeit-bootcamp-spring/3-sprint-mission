package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class UserException extends DiscodeitException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected UserException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected UserException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
