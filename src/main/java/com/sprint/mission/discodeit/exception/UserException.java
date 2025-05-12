package com.sprint.mission.discodeit.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class UserException extends BusinessException {

  public UserException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static UserException notFound(UUID userId) {
    return new UserException(
        ErrorCode.NOT_FOUND,
        MessageFormat.format("유저를 찾을 수 없습니다. [UserID: {0}]", userId)
    );
  }

  public static UserException notParticipant() {
    return new UserException(ErrorCode.UNAUTHORIZED, "채널 참여자가 아닙니다.");
  }

  public static UserException duplicateEmail() {
    return new UserException(ErrorCode.ALREADY_EXISTS, "이미 사용 중인 이메일입니다.");
  }

  public static UserException duplicateName() {
    return new UserException(ErrorCode.ALREADY_EXISTS, "이미 사용 중인 이름입니다.");
  }
}
