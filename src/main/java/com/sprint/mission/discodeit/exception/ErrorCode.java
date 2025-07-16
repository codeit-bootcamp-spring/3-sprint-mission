package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 유저
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_USER("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),

    // 로그인
    MISSING_PASSWORD("올바른 비밀번호가 아닙니다.", HttpStatus.BAD_REQUEST),

    // 채널
    CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRIVATE_CHANNEL_UPDATE("private 채널은 수정할 수 없습니다.", HttpStatus.FORBIDDEN),
    CURSOR_INVALID("잘못된 커서 형식입니다. ISO-8601 형식이어야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_CHANNEL_PARTICIPANT("유효하지 않은 채널 참여자 ID가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),

    // 메시지
    MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MESSAGE_EMPTY("메시지 내용과 첨부파일이 모두 비어있습니다.", HttpStatus.BAD_REQUEST),

    // 파일
    FILE_NOT_FOUND("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_FILE("파일이 이미 존재합니다.", HttpStatus.CONFLICT),
    FILE_UPLOAD_INVALID("파일 업로드 대상 또는 내용이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    FILE_SAVE_FAILED("파일 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DOWNLOAD_FAILED("파일 다운로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_INIT_FAILED("파일 초기화 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_READ_FAILED("파일을 불러오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // readStatus
    READSTATUS_NOT_FOUND("ReadStatus를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    READSTATUS_ALREADY_EXISTS("ReadStatus가 이미 존재합니다.", HttpStatus.CONFLICT),

    // userStatus
    USERSTATUS_NOT_FOUND("UserStatus를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_USERSTATUS("userStatus가 이미 존재합니다.", HttpStatus.CONFLICT);

    private final String message;
    private final int status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status.value();
    }

}
