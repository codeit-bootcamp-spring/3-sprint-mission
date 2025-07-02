
package com.sprint.mission.discodeit.exception;

import lombok.Builder;

@Builder
public record ErrorResponse(
    int status,
    String message,
    String exceptionType,
    String details
) {}
