package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateNameException extends UserException {
  public DuplicateNameException() {
    super(ErrorCode.USER_ALREADY_EXISTS, "이미 사용 중인 이름입니다.");
  }
}
