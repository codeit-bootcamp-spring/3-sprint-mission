package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundByUserException extends UserStatusException {

    public UserStatusNotFoundByUserException(UUID userId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND_BY_USER, Map.of("활동 상태를 조회하려고 한 유저: ", userId));
    }
}
