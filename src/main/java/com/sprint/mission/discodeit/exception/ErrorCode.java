package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : ErrorCode
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */


@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "유저가 이미 있습니다."),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."),
    PRIVATE_CHANNEL_UPDATE(HttpStatus.BAD_REQUEST, "프라이빗 채널은 수정이 불가능합니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation 검증에 실패했습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "메세지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
    }
