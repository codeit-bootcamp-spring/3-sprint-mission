package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

/**
 * UserStatus가 존재하지 않을 때 발생하는 예외입니다.
 */
public class UserStatusNotFoundException extends UserStatusException {
    public UserStatusNotFoundException(String message) {
        super(message, ErrorCode.USER_STATUS_NOT_FOUND);
    }
} 