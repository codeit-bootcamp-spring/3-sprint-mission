package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class DuplicateUserException extends UserException {

    public DuplicateUserException(String username, String email) {
        super(
            Instant.now(),
            ErrorCode.DUPLICATE_USER,
            Map.of("username", username, "email", email)
        );
    }
}
