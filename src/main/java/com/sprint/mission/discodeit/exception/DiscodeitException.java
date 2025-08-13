package com.sprint.mission.discodeit.exception;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 애플리케이션 전반에서 사용되는 공통 런타임 예외 클래스입니다.
 * <p>도메인별 예외의 상위 클래스 역할을 하며, 타임스탬프, 에러코드, 상세정보를 포함할 수 있습니다.</p>
 */
@RequiredArgsConstructor
public class DiscodeitException extends RuntimeException {

    /** 예외 발생 시각 */
    final Instant timestamp;
    /** 에러 코드 */
    final ErrorCode errorCode;
    /** 추가 상세 정보 */
    final Map<String, Object> details;

    /**
     * 메시지만 전달받는 생성자 (기본값 사용)
     * @param message 예외 메시지
     */
    public DiscodeitException(String message) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = null;
        this.details = null;
    }

    /**
     * 모든 정보를 전달받는 생성자
     * @param message 예외 메시지
     * @param timestamp 예외 발생 시각
     * @param errorCode 에러 코드
     * @param details 추가 상세 정보
     */
    public DiscodeitException(String message, Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        super(message);
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * 예외 발생 시각 반환
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    /**
     * 에러 코드 반환
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    /**
     * 상세 정보 반환
     */
    public Map<String, Object> getDetails() {
        return details;
    }
}
