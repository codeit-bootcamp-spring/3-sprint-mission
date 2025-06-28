package com.sprint.mission.discodeit.exception.User;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserPasswordMismatchException extends UserException {

    private final UUID userId;

    public UserPasswordMismatchException(UUID userId) {
        super(
                ErrorCode.PASSWORD_MISMATCH,
                Map.of("userId", userId)
        );
        this.userId = userId;
    }
}
