package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException {

  private UUID userId;

  public UserStatusAlreadyExistsException(UUID userId) {
    super(ErrorCode.DUPLICATE_USER_STATUS, Map.of("userId", userId));
  }
}
