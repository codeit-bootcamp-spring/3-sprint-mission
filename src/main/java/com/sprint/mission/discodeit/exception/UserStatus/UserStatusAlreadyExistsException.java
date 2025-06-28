package com.sprint.mission.discodeit.exception.UserStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatusAlreadyExistsException extends UserStatusException {

    private final UUID userStatusId;

    public UserStatusAlreadyExistsException(UUID userStatusId) {
        super(
                ErrorCode.DUPLICATE_USER_STATUS,
                Map.of("userStatusId", userStatusId)
        );
        this.userStatusId = userStatusId;
    }
}
