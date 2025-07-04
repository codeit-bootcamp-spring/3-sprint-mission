package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PasswordNotMatchException extends AuthException {

  public PasswordNotMatchException() {
    super(ErrorCode.USER_PASSWORD_NOT_MATCHED, Map.of("password", "비밀번호가 일치하지 않습니다."));
  }
}
