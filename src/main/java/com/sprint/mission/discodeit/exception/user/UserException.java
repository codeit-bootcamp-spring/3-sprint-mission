package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

/**
 * 사용자(User) 도메인에서 발생하는 예외의 상위 클래스입니다.
 * <p>구체적인 사용자 예외는 이 클래스를 상속받아 구현합니다.</p>
 */
public abstract class UserException extends DiscodeitException {
    /**
     * 메시지만 전달받는 생성자 (기본값 사용)
     * @param message 예외 메시지
     */
    public UserException(String message) {
        super(message, Instant.now(), ErrorCode.USER_NOT_FOUND, Map.of());
    }
    /**
     * 메시지와 에러코드를 전달받는 생성자
     * @param message 예외 메시지
     * @param errorCode 에러 코드
     */
    public UserException(String message, ErrorCode errorCode) {
        super(message, Instant.now(), errorCode, Map.of());
    }
    /**
     * 메시지, 에러코드, 상세정보를 전달받는 생성자
     * @param message 예외 메시지
     * @param errorCode 에러 코드
     * @param details 추가 상세 정보
     */
    public UserException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, Instant.now(), errorCode, details);
    }
}
