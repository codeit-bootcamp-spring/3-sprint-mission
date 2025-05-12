package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;

public record ErrorResponse(
    String errorCode,
    String message,
    int status,
    Instant timestamp
) {

}
