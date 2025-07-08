package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User 도메인
    DUPLICATED_USER(HttpStatus.BAD_REQUEST, "001_DUPLICATED_EMAIL", "이미 가입된 이메일입니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "002_INVALID_ARGUMENT", "누락된 정보가 있는지 확인해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "003_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "004_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),

    // UserStatus 도메인
    USER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "101_USER_STATUS_NOT_FOUND", "유저 상태 정보를 찾을 수 없습니다."),
    DUPLICATED_USER_STATUS(HttpStatus.BAD_REQUEST, "102_DUPLICATED_USER_STATUS", "이미 등록된 유저 상태입니다."),

    // Channel 도메인
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "201_CHANNEL_NOT_FOUND", "채널을 찾을 수 없습니다."),
    DUPLICATED_CHANNEL(HttpStatus.BAD_REQUEST, "202_DUPLICATED_CHANNEL", "이미 존재하는 채널입니다."),
    PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "203_PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED", "비공개 채널은 수정할 수 없습니다."),

    // Message 도메인
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "301_MESSAGE_NOT_FOUND", "메시지를 찾을 수 없습니다."),
    DUPLICATED_MESSAGE(HttpStatus.BAD_REQUEST, "302_DUPLICATED_MESSAGE", "이미 존재하는 메시지입니다."),
    MESSAGE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "303_MESSAGE_PERMISSION_DENIED", "메시지에 대한 권한이 없습니다."),

    // ReadStatus 도메인
    READ_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "401_READ_STATUS_NOT_FOUND", "읽음 상태 정보를 찾을 수 없습니다."),
    DUPLICATED_READ_STATUS(HttpStatus.BAD_REQUEST, "402_DUPLICATED_READ_STATUS", "이미 존재하는 읽음 상태입니다."),

    // BinaryContent 도메인
    BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "501_BINARY_CONTENT_NOT_FOUND", "파일(바이너리 컨텐츠)을 찾을 수 없습니다."),
    DUPLICATED_BINARY_CONTENT(HttpStatus.BAD_REQUEST, "502_DUPLICATED_BINARY_CONTENT", "이미 존재하는 파일(바이너리 컨텐츠)입니다."),

    // 유효성 검사
    METHOD_ARGUE_NOT_VALID(HttpStatus.BAD_REQUEST, "601_METHOD_ARGU_NOT_VALID", "유효하지 않은 입력값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String msg;
}
