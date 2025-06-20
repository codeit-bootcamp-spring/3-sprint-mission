package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistsException extends UserStatusException {

    public UserStatusAlreadyExistsException(UUID userStatusId) {
        super(ErrorCode.UserStatus_Already_Exists, Map.of("userStatusId", userStatusId));
    }
}
