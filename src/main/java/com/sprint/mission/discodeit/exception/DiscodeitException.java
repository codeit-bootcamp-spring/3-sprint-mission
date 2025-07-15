package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@Getter
public class DiscodeitException extends RuntimeException {

    final Instant timestamp;
    final ErrorCode errorCode;
    final Map<String, Object> details;
}
