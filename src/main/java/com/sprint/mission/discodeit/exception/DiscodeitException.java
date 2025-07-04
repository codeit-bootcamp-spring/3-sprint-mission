package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {

    final Instant timestamp;
    final ErrorCode errorCode;
    final Map<String, Object> details;

    public DiscodeitException(Instant timestamp, ErrorCode errorCode, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = details == null
                ? Map.of()
                : Map.copyOf(details); // 불변 Map으로 복사
    }
}
