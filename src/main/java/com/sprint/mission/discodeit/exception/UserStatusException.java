package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserStatusException extends BusinessException {

  public UserStatusException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static UserStatusException notFound(UUID id) {
    return new UserStatusException(
        ErrorCode.NOT_FOUND,
        "UserStatus를 찾을 수 없습니다. [ID: " + id + "]"
    );
  }

  public static UserStatusException notFoundByUserId(UUID userId) {
    return new UserStatusException(
        ErrorCode.NOT_FOUND,
        "UserStatus를 찾을 수 없습니다. [UserID: " + userId + "]"
    );
  }

  public static UserStatusException duplicate(UUID userId) {
    return new UserStatusException(
        ErrorCode.ALREADY_EXISTS,
        "이미 존재하는 UserStatus입니다. [UserID: " + userId + "]"
    );
  }
}
