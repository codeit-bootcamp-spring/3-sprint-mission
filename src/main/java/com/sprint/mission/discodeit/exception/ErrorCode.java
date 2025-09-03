package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_USERNAME("This username is already taken.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("This email is already registered.", HttpStatus.CONFLICT),
    DUPLICATE_BOTH("Both username and email are already in use.", HttpStatus.CONFLICT),
    BINARY_CONTENT_NOT_FOUND("BinaryContent not found with ID: %s", HttpStatus.NOT_FOUND),
    PROFILE_IMAGE_PROCESSING_FAILED("Failed to process the uploaded profile image: %s",
        HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found: %s", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Invalid password.", HttpStatus.UNAUTHORIZED),
    USER_STATUS_NOT_FOUND("UserStatus not found with ID: %s", HttpStatus.NOT_FOUND),
    USER_STATUS_ALREADY_EXISTS("UserStatus already exists for userId: %s", HttpStatus.CONFLICT),

    READ_STATUS_NOT_FOUND("ReadStatus with ID: %s not found", HttpStatus.NOT_FOUND),
    READ_STATUS_ALREADY_EXISTS("ReadStatus with userId %s and channelId %s already exists",
        HttpStatus.CONFLICT),
    CHANNEL_NOT_FOUND("Channel not found with ID: %s", HttpStatus.NOT_FOUND),
    PRIVATE_CHANNEL_UPDATE("Private channels cannot be updated.", HttpStatus.BAD_REQUEST),
    MESSAGE_NOT_FOUND("Message with ID: %s not found", HttpStatus.NOT_FOUND),

    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
