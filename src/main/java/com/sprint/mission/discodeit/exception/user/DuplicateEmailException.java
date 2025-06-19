package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.entity.ErrorCode;

import java.util.Map;

public class DuplicateEmailException extends UserException {

    public DuplicateEmailException(String email) {
        super(
                ErrorCode.DUPLICATE_USER_EMAIL.getMessage() + " email: " + email,
                ErrorCode.DUPLICATE_USER_EMAIL,
                Map.of("email", email)
        );
    }
}
