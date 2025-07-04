package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;


@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;
    private Instant timestamp;
}
