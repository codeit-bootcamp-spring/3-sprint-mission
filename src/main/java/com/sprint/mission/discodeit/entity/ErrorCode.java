package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
    DUPLICATE_USER_NAME("이미 존재하는 Username입니다."),
    DUPLICATE_USER_EMAIL("이미 존재하는 이메일입니다."),
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("Private Channel은 수정할 수 없습니다."),
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다."),
    LOGIN_FAILED("로그인이 실패했습니다. 비밀번호를 확인해주세요."),
    BINARY_CONTENT_NOT_FOUND("파일을 찾을 수 없습니다."),
    READ_STATUS_NOT_FOUND("읽음 상태를 찾을 수 없습니다."),
    USER_STATUS_NOT_FOUND("사용자 상태를 찾을 수 없습니다."),
    READ_STATUS_ALREADY_EXIST("이미 존재하는 읽음 상태입니다."),
    USER_STATUS_ALREADY_EXIST("이미 존재하는 사용자 상태입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
