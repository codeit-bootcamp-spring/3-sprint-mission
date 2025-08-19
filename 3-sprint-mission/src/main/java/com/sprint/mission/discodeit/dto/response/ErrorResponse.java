package com.sprint.mission.discodeit.dto.response;

public record ErrorResponse(

        String message,
        int status
) {

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(message, status);
    }
}
