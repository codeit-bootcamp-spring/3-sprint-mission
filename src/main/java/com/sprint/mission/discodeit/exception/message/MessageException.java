package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

/**
 * 메시지(Message) 도메인에서 발생하는 예외의 상위 클래스입니다.
 * <p>구체적인 메시지 예외는 이 클래스를 상속받아 구현합니다.</p>
 */
public abstract class MessageException extends DiscodeitException {
    /**
     * 메시지만 전달받는 생성자 (기본값 사용)
     * @param message 예외 메시지
     */
    public MessageException(String message) {
        super(message, Instant.now(), ErrorCode.INVALID_ARGUMENT, Map.of());
    }
    /**
     * 메시지와 에러코드를 전달받는 생성자
     * @param message 예외 메시지
     * @param errorCode 에러 코드
     */
    public MessageException(String message, ErrorCode errorCode) {
        super(message, Instant.now(), errorCode, Map.of());
    }
    /**
     * 메시지, 에러코드, 상세정보를 전달받는 생성자
     * @param message 예외 메시지
     * @param errorCode 에러 코드
     * @param details 추가 상세 정보
     */
    public MessageException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, Instant.now(), errorCode, details);
    }
}
