package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException{

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    /**
     * 기본 생성자
     */
    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }

    /**
     * detail 값과 함께 생성
     */
    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details){
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? Map.copyOf(details) : Map.of();
    }

    /**
     * 예외와 함께 생성
     */
    public DiscodeitException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }

    /**
     * 모든 정보로 생성
     */
    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause){
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? Map.copyOf(details) : Map.of();
    }

    /**
     * 커스텀 메시지와 함께 생성
     */
    public DiscodeitException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }

    /**
     * 커스텀 메시지와 details로 예외 생성
     */
    public DiscodeitException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(customMessage);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? Map.copyOf(details) : Map.of();
    }

}
