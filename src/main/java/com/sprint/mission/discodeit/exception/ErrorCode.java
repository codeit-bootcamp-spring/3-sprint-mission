package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
    DUPLICATE_USERNAME("This username is already taken."),
    DUPLICATE_EMAIL("This email is already registered."),
    DUPLICATE_BOTH("Both username and email are already in use."),
    BINARY_CONTENT_NOT_FOUND("BinaryContent not found with ID: %s"),
    PROFILE_IMAGE_PROCESSING_FAILED("Failed to process the uploaded profile image: %s"),
    USER_NOT_FOUND("User not found: %s"),
    INVALID_PASSWORD("Invalid password."),
    USER_STATUS_NOT_FOUND("UserStatus not found with ID: %s"),
    USER_STATUS_ALREADY_EXISTS("UserStatus already exists for userId: %s"),

    READ_STATUS_NOT_FOUND("ReadStatus with ID: %s not found"),
    READ_STATUS_ALREADY_EXISTS("ReadStatus with userId %s and channelId %s already exists"),
    CHANNEL_NOT_FOUND("Channel not found with ID: %s"),
    PRIVATE_CHANNEL_UPDATE("Private channels cannot be updated."),
    MESSAGE_NOT_FOUND("Message with ID: %s not found");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
