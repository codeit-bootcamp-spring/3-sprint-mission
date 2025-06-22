package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class CommonException extends DiscodeitException {

  public CommonException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected CommonException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected CommonException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
