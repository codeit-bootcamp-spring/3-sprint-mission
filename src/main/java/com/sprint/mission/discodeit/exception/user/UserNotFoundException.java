package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 사용자를 찾을 수 없을 때 발생하는 예외입니다.
 */
public class UserNotFoundException extends UserException {
    /**
     * 메시지만 전달받는 생성자
     * @param message 예외 메시지
     */
    public UserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }
    /**
     * 메시지와 상세정보를 전달받는 생성자
     * @param message 예외 메시지
     * @param details 추가 상세 정보
     */
    public UserNotFoundException(String message, Map<String, Object> details) {
        super(message, ErrorCode.USER_NOT_FOUND, details);
    }
}
