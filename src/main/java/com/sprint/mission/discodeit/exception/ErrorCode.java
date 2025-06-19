package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
    DUPLICATE_USERNAME("This username is already taken."),
    DUPLICATE_EMAIL("This email is already registered."),
    DUPLICATE_BOTH("Both username and email are already in use."),
    BINARY_CONTENT_NOT_FOUND("BinaryContent not found with ID: %s"),
    PROFILE_IMAGE_PROCESSING_FAILED("Failed to process the uploaded profile image: %s"),
    USER_NOT_FOUND("User not found: %s"),
    INVALID_PASSWORD("Invalid password.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
