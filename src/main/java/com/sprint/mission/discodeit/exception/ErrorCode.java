package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 사용자 상태
    USER_STATUS_ALREADY_EXISTS("이미 UserStatus가 존재합니다."),
    USER_STATUS_NOT_FOUND("UserStatus를 찾을 수 없습니다."),

    // 사용자
    EMAIL_ALREADY_EXISTS("이미 등록된 Email입니다."),
    USERNAME_ALREADY_EXISTS("이미 사용 중인 UserName입니다."),
    USER_NOT_FOUND("User를 찾을 수 없습니다."),
    INVALID_USER_UPDATE_INPUT("사용자명은 2자 이상 10자 이하여야 합니다."),

    // 읽음 상태
    READ_STATUS_ALREADY_EXISTS("이미 ReadStatus가 존재합니다."),
    READ_STATUS_NOT_FOUND("ReadStatus를 찾을 수 없습니다."),

    // 메시지
    MESSAGE_NOT_FOUND("Message를 찾을 수 없습니다."),
    INVALID_MESSAGE_CONTENT("내용 또는 첨부파일 중 하나는 반드시 존재해야 합니다."),

    // 채널
    CHANNEL_NOT_FOUND("Channel을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_MODIFICATION("Private Channel은 수정할 수 없습니다."),

    // 파일
    BINARY_CONTENT_NOT_FOUND("BinaryContent를 찾을 수 없습니다."),
    FILE_PROCESSING_FAILED("이미지 처리 중 오류가 발생했습니다."),

    // 로그인
    INVALID_USERNAME_OR_PASSWORD("username 또는 password가 틀렸습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
