package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : DiscodeitException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
@Getter
@AllArgsConstructor
@ToString
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    /**
     * 조회 시도한 사용자의 ID 정보
     * 업데이트 시도한 PRIVATE 채널의 ID 정보
     */
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = null;
    }
}
