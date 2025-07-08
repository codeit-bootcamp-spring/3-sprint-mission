package com.sprint.mission.discodeit.exception.UserStatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class UserStatusException extends DiscodeitException {

  public UserStatusException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected UserStatusException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected UserStatusException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
