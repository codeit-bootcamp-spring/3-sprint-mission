package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 비밀번호가 일치하지 않을 때 발생하는 예외입니다.
 */
public class InvalidPasswordException extends UserException {
    public InvalidPasswordException(String message) {
        super(message, ErrorCode.INVALID_PASSWORD);
    }
    public InvalidPasswordException(String message, Map<String, Object> details) {
        super(message, ErrorCode.INVALID_PASSWORD, details);
    }
} 