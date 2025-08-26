package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidTokenException extends AuthException {

  public InvalidTokenException(String reason) {
    super(ErrorCode.UNAUTHORIZED, reason);
  }
}
