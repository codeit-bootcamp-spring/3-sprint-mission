package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

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
    UPLOAD_IMAGE_S3_RETRIED_FAILED("재시도 끝에 S3에 업로드하는데 실패하였습니다 "),

    // 로그인
    INVALID_USERNAME_OR_PASSWORD("username 또는 password가 틀렸습니다."),

    // 토큰
    INVALID_JWT_TOKEN("유효하지 않은 토큰입니다."),
    FAILED_TOKEN_GENERATED("토큰 생성에 실패하였습니다"),
    INVALID_PRINCIPAL("잘못된 형태의 Principal입니다"),

    // 알림
    NOTIFICATION_NOT_FOUND("알림이 존재하지 않습니다"),
    NOTIFICATION_ACCESS_DENIED("다른 사용자의 알림은 확인할 수 없습니다");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
