package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UsernameNotFoundException extends AuthException {

  public UsernameNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND, "username이 일치하지 않습니다.");
  }
}
