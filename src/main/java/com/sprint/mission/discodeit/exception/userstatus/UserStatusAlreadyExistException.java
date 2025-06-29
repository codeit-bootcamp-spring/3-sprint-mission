package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistException extends UserStatusException {

    public UserStatusAlreadyExistException(UUID userId) {
        super(ErrorCode.USER_STATUS_ALREADY_EXIST, Map.of("활동상태를 추가하려고 한 유저: ", userId));
    }
}
