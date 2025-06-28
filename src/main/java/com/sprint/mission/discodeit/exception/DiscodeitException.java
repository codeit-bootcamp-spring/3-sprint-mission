package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DiscodeitException extends RuntimeException {

    public final Instant timestamp;
    public final ErrorCode errorCode;
    public final Map<String, Object> details;   // 예외 발생 상황에 대한 추가정보

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        this(Instant.now(), errorCode, details);
    }
}
