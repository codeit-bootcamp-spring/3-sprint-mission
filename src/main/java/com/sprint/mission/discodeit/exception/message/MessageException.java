package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class MessageException extends DiscodeitException {

  protected MessageException(ErrorCode errorCode, String reason) {
    super(errorCode, reason);
  }
}