package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * API 에러 응답을 위한 DTO.
 */
@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final Instant timestamp;
    @Builder.Default
    private final Map<String, Object> details = Collections.emptyMap();
}
