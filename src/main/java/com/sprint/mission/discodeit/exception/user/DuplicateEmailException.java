package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateEmailException extends UserException {
  public DuplicateEmailException() {
    super(ErrorCode.USER_ALREADY_EXISTS, "이미 사용 중인 이메일입니다.");
  }
}
