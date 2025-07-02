package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 이미 존재하는 사용자(이메일/아이디)로 가입 시도할 때 발생하는 예외입니다.
 */
public class DuplicatedUserException extends UserException {
    public DuplicatedUserException(String message) {
        super(message, ErrorCode.DUPLICATED_USER);
    }
    public DuplicatedUserException(String message, Map<String, Object> details) {
        super(message, ErrorCode.DUPLICATED_USER, details);
    }
} 