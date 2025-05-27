package com.sprint.mission.discodeit.controller.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATED_MEMBER(HttpStatus.BAD_REQUEST, "001_DUPLICATED_EMAIL", "이미 가입된 이메일입니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "002_INVALID_ARGUMENT", "누락된 정보가 있는지 확인해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "003_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "004_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String msg;
}
