package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("U001", HttpStatus.NOT_FOUND , "사용자를 조회할 수 없습니다."),
    DUPLICATE_USER("U002", HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),

    AUTHENTICATION_REQUIRED("A001", HttpStatus.UNAUTHORIZED ,"인증이 필요합니다."),
    ACCESS_DENIED("A0012", HttpStatus.FORBIDDEN ,"접근 권한이 없습니다."),
    INVALID_JWT_TOKEN("", HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),

    CHANNEL_NOT_FOUND("CH001", HttpStatus.NOT_FOUND, "채널을 조회할 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE("CH002", HttpStatus.BAD_REQUEST, "비공개 채널은 수정할 수 없습니다."),

    MESSAGE_NOT_FOUND("M001", HttpStatus.NOT_FOUND ,"메시지를 조회할 수 없습니다."),

    FILE_NOT_FOUND("F001", HttpStatus.NOT_FOUND, "파일을 조회할 수 없습니다."),
    FILE_STORAGE_ERROR("F002", HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    RESOURCE_URL_CREATION_ERROR("F003", HttpStatus.INTERNAL_SERVER_ERROR, "파일 URL 리소스 생성에 실패했습니다."),
    BINARY_CONTENT_CREATION_FAILED("F004", HttpStatus.INTERNAL_SERVER_ERROR, "바이너리 컨텐츠 생성에 실패했습니다."),

    READSTATUS_NOT_FOUND("R001", HttpStatus.NOT_FOUND, "읽음 상태를 조회할 수 없습니다."),
    DUPLICATE_READSTATUS("R002", HttpStatus.BAD_REQUEST, "이미 존재하는 읽음 상태입니다."),

    USERSTATUS_NOT_FOUND("S001", HttpStatus.NOT_FOUND, "사용자 상태를 조회할 수 없습니다."),
    DUPLICATE_USERSTATUS("S002", HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 상태입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}