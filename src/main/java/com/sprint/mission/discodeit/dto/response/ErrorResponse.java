package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.time.Instant;
import java.util.Map;

/**
 * 일관된 예외 응답을 정의하는 클래스
 * <pre>
 * int status : HTTP 상태코드
 * String exception : 발생한 예외 클래스 이름
 * </pre>
 */
public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

    public static ErrorResponse of(DiscodeitException ex) {
        return new ErrorResponse(
            ex.getTimestamp(),
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(), // ErrorCode의 메시지 사용
            ex.getDetails(),
            ex.getClass().getSimpleName(),
            ex.getErrorCode().getStatus()
        );
    }
}
