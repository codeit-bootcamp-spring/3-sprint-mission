package com.sprint.mission.discodeit.exception;

public enum ErrorCode {
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다."),
    CHANNEL_NOT_FOUND("C001", "채널을 찾을 수 없습니다."),
    CHANNEL_PRIVATE_UPDATE_NOT_ALLOWED("C002", "비공개 채널은 수정할 수 없습니다."),
    MESSAGE_NOT_FOUND("M001", "메시지를 찾을 수 없습니다."),
    INVALID_ARGUMENT("C999", "잘못된 요청입니다."),
    BINARY_CONTENT_NOT_FOUND("B001", "첨부 파일을 찾을 수 없습니다.")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
