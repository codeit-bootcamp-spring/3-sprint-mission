package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class AuthException extends DiscodeitException {

  protected AuthException(ErrorCode errorCode, String reason) {
    super(errorCode, java.util.Map.of("reason", reason));
  }
}
