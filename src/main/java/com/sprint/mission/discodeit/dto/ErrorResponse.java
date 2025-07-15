package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.dto
 * FileName     : ErrorResponse
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
@Getter
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;
}
