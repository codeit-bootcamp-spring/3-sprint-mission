package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNKNOWN_ERROR(500, "E000", "알 수 없는 에러가 발생했습니다. 서버 내부 오류"),

    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(409, "U003", "이미 존재하는 이메일입니다."),
    USER_NAME_ALREADY_EXISTS(409, "U004", "이미 존재하는 이름입니다."),

    CHANNEL_NOT_FOUND(404, "C001", "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE_FORBIDDEN(403, "C003", "비공개 채널은 수정할 수 없습니다."),

    MESSAGE_NOT_FOUND(404, "M001", "메시지를 찾을 수 없습니다."),
    MESSAGE_DELETE_FORBIDDEN(403, "M002", "해당 메시지를 삭제할 권한이 없습니다."),

    READ_STATUS_NOT_FOUND(404, "R001", "읽기 상태을 찾을 수 없습니다."),
    READ_STATUS_ALREADY_EXISTS(409, "R002", "해당 유저와 채널에 대한 읽기 상태가 이미 존재합니다."),

    USER_STATUS_NOT_FOUND(404, "US001", "사용자 상태를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS(409, "US002", "이미 존재하는 사용자 상태입니다."),

    BINARY_CONTENT_NOT_FOUND(404, "B001", "첨부파일을 찾을 수 없습니다."),
    BINARY_CONTENT_INVALID(400, "B002", "유효하지 않은 첨부파일입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
