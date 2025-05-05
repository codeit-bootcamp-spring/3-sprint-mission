package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

public class UserException extends BusinessException {

  public static final String USER_NOT_FOUND = "U001";
  public static final String USER_NOT_PARTICIPANT = "U002";
  public static final String DUPLICATE_EMAIL = "U003";
  public static final String DUPLICATE_NAME = "U004";

  public UserException(String errorCode, String message) {
    super(errorCode, message);
  }

  public static UserException notFound(UUID userId) {
    return new UserException(USER_NOT_FOUND, "유저를 찾을 수 없습니다. [UserID: " + userId + "]");
  }

  public static UserException notParticipant() {
    return new UserException(USER_NOT_PARTICIPANT, "채널 참여자가 아닙니다.");
  }

  public static UserException duplicateEmail() {
    return new UserException(DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다.");
  }

  public static UserException duplicateName() {
    return new UserException(DUPLICATE_NAME, "이미 사용 중인 이름입니다.");
  }
}
