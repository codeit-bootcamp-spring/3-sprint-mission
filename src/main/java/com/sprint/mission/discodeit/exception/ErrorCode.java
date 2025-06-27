package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // User 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 사용자입니다."),
    USER_INVALID_INPUT(HttpStatus.BAD_REQUEST, "USER_003", "사용자 정보가 유효하지 않습니다."),
    USER_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER_999", "사용자 처리 중 알 수 없는 오류가 발생했습니다."),

    // Channel 관련 에러
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "CHANNEL_001", "채널을 찾을 수 없습니다."),
    CHANNEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "CHANNEL_002", "이미 존재하는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "CHANNEL_003", "비공개 채널은 수정할 수 없습니다."),
    CHANNEL_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CHANNEL_999",
        "채널 처리 중 알 수 없는 오류가 발생했습니다."),

    // Message 관련 에러
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "메시지를 찾을 수 없습니다."),
    MESSAGE_INVALID_CONTENT(HttpStatus.BAD_REQUEST, "MESSAGE_002", "메시지 내용이 유효하지 않습니다."),
    MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "MESSAGE_003", "메시지가 너무 깁니다."),
    MESSAGE_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MESSAGE_999",
        "메시지 처리 중 알 수 없는 오류가 발생했습니다."),

    //BinaryContent 관련 에러
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BINARY_CONTENT_001", "첨부파일을 찾을 수 없습니다."),
    BINARY_CONTENT_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BINARY_CONTENT_999",
        "첨부 파일을 처리 중 알 수 없는 오류가 발생했습니다."),

    //ReadStatus 관련 에러
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "READ_STATUS_001", "읽기 상태을 찾을 수 없습니다."),
    READ_STATUS_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "READ_STATUS_999",
        "읽기 상태 처리 중 알 수 없는 오류가 발생했습니다."),

    //UserStatus 관련 에러
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_STATUS_001", "유저 상태을 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_STATUS_002", "이미 존재하는 유저 상태입니다."),
    USER_STATUS_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "USER_STATUS_999",
        "유저 상태 처리 중 알 수 없는 오류가 발생했습니다."),

    //Validation 관련 에러
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_001", "조건에 맞지 않는 입력값입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
