package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다. "),
    USER_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND, "해당하는 이름으로 유저를 찾을 수 없습니다. "),
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "이미 존재하는 유저 이름입니다. "),
    DUPLICATE_USER_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 유저 이메일입니다. "),
    INVALID_USER_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다. "),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다. "),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.FORBIDDEN, "비공개 채널은 수정할 수 없습니다. "),

    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다. "),

    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "바이너리 컨텐츠를 찾을 수 없습니다. "),
    RESOURCE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "바이너리 컨텐츠 로드에 실패했습니다. "),

    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 접속 상태를 찾을 수 없습니다. "),
    USER_STATUS_NOT_FOUND_BY_USER(HttpStatus.NOT_FOUND, "유저 접속 상태를 찾을 수 없습니다. "),
    USER_STATUS_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 user status 입니다. "),

    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 읽음 상태를 찾을 수 없습니다. "),
    READ_STATUS_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 read status 입니다. "),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다. ");

    private final String message;
    private final HttpStatus status;

    ErrorCode(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    // HTTP 상태 코드 숫자값 반환
    public int getStatus() {
        return this.status.value();
    }

     // HttpStatus 객체를 반환
    public HttpStatus getHttpStatus() {
        return this.status;
    }
}
