package com.sprint.mission.discodeit.exception.Message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class MessageException extends DiscodeitException {

  public MessageException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected MessageException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected MessageException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
