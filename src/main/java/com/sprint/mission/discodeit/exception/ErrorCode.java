package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // User
    USER_NOT_FOUND("존재하지 않는 사용자입니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),
    PASSWORD_MISMATCH("패스워드가 일치하지 않습니다."),

    // Channel
    CHANNEL_NOT_FOUND("존재하지 않는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE("PRIVATE 채널은 수정할 수 없습니다."),

    // Message
    MESSAGE_NOT_FOUND("존재하지 않는 메시지입니다."),

    // BinaryContent
    BINARY_CONTENT_NOT_FOUND("존재하지 않는 컨텐츠입니다."),

    // ReadStatus
    READ_STATUS_NOT_FOUND("존재하지 않는 ReadStatus입니다."),
    DUPLICATE_READ_STATUS("이미 존재하는 ReadStatus입니다."),

    // UserStatus
    USER_STATUS_NOT_FOUND("존재하지 않는 UserStatus입니다."),
    DUPLICATE_USER_STATUS("이미 존재하는 UserStatus입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
