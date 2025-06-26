package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class DuplicateUserStatusException extends UserStatusException {

    public DuplicateUserStatusException(UUID userId) {
        super(ErrorCode.DUPLICATE_USERSTATUS, Map.of("userId", userId));
    }
}