package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class UserStatusException extends DiscodeitException {

  protected UserStatusException(ErrorCode errorCode, String reason) {
    super(errorCode, reason);
  }
}
