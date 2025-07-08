package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class ChannelException extends DiscodeitException {

  public ChannelException(ErrorCode errorCode) {
    super(errorCode);
  }

  protected ChannelException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected ChannelException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
