package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;


@Getter
@AllArgsConstructor
public class ErrorResponse {
    String code;
    String message;
    Map<String, Object> details;
    String exceptionType;
    int status;
    private Instant timestamp;
}
