package com.sprint.mission.discodeit.dto;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUCCESS("SUCCESS", HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    REQUEST_FAIL("REQUEST_FAIL", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CHANNEL_NOT_FOUND("CHANNEL_NOT_FOUND", HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND("MESSAGE_NOT_FOUND", HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    READ_STATUS_NOT_FOUND("READ_STATUS_NOT_FOUND", HttpStatus.NOT_FOUND, "읽음 상태를 찾을 수 없습니다."),
    BINARY_NOT_FOUND("BINARY_NOT_FOUND", HttpStatus.NOT_FOUND, "바이너리 파일을 찾을 수 없습니다."),
    ID_OR_PASSWORD_VALID("ID_OR_PASSWORD_VALID", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 유효하지 않습니다."),
    USER_OR_CHANNEL_NOT_FOUND("USER_OR_CHANNEL_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자 또는 채널이 존재하지 않습니다."),
    DUPLICATE_USER("DUPLICATE_USER", HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    DUPLICATE_READ_STATUS("DUPLICATE_READ_STATUS", HttpStatus.CONFLICT, "중복된 읽음 상태입니다."),
    FILE_PROCESSING_ERROR("FILE_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    DUPLICATE_USER_STATUS("DUPLICATE_USER_STATUS", HttpStatus.CONFLICT, "중복된 사용자 상태입니다."),
    USER_STATUS_NOT_FOUND("USER_STATUS_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자 상태 정보를 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE_DENIED("PRIVATE_CHANNEL_UPDATE_DENIED", HttpStatus.FORBIDDEN, "PRIVATE 채널은 수정할 수 없습니다."),
    TOKEN_NOT_VALID("TOKEN_NOT_VALID", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

