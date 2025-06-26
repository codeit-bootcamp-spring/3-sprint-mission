package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
    USER_NOT_FOUND("사용자가 존재하지 않습니다."),
    DUPLICATE_USER_NAME("사용자명이 중복됩니다. 다른 사용자명을 사용해주세요."),
    DUPLICATE_USER_EMAIL("사용자 이메일이 중복됩니다. 다른 이메일을 사용해주세요."),


    CHANNEL_NOT_FOUND("채널이 존재하지 않습니다."),
    PRIVATE_CHANNEL_UPDATE("PRIVATE 채널은 업데이트 할 수 없습니다."),

    MESSAGE_NOT_FOUND("메시지가 존재하지 않습니다."),
    
    AUTHOR_NOT_FOUND("작성자가 존재하지 않습니다."),

    WRONG_PASSWORD("비밀번호가 올바르지 않습니다."),

    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),

    READSTATUS_NOT_FOUND("ReadStatus가 존재하지 않습니다."),
    DUPLICATE_READSTATUS("해당 유저id, 채널 id에 이미 존재하는 ReadStatus 입니다."),

    USERSTATUS_NOT_FOUND("UserStatus가 존재하지 않습니다."),
    DUPLICATE_USERSTATUS("해당 UserStatus가 이미 존재합니다.");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
