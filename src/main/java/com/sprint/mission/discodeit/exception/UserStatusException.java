package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserStatusException extends BusinessException {

  public static final String USER_STATUS_NOT_FOUND = "US001";
  public static final String USER_STATUS_NOT_FOUND_BY_USER_ID = "US002";
  public static final String DUPLICATE_USER_STATUS = "US003";

  public UserStatusException(String errorCode, String message) {
    super(errorCode, message);
  }

  public static UserStatusException notFound(UUID id) {
    return new UserStatusException(
        USER_STATUS_NOT_FOUND,
        "UserStatus를 찾을 수 없습니다. [ID: " + id + "]"
    );
  }

  public static UserStatusException notFoundByUserId(UUID userId) {
    return new UserStatusException(
        USER_STATUS_NOT_FOUND_BY_USER_ID,
        "UserStatus를 찾을 수 없습니다. [UserID: " + userId + "]"
    );
  }

  public static UserStatusException duplicate(UUID userId) {
    return new UserStatusException(
        DUPLICATE_USER_STATUS,
        "이미 존재하는 UserStatus입니다. [UserID: " + userId + "]"
    );
  }
}
