package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class ChannelException extends DiscodeitException {

  protected ChannelException(ErrorCode errorCode, String reason) {
    super(errorCode, java.util.Map.of("reason", reason));
  }
}
