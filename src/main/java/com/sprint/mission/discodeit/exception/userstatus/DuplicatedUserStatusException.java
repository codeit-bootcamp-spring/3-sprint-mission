package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * UserStatus가 중복될 때 발생하는 예외입니다.
 */
public class DuplicatedUserStatusException extends UserStatusException {
    public DuplicatedUserStatusException(String message) {
        super(message, ErrorCode.DUPLICATED_USER_STATUS);
    }
}