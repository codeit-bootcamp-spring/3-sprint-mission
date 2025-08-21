package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class TokenGenerationException extends AuthException {

  public TokenGenerationException(String reason) {
    super(ErrorCode.TOKEN_GENERATION_ERROR, reason);
  }
}
