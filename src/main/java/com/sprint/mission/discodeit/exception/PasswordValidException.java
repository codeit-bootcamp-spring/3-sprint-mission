package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import org.springframework.http.HttpStatus;

public class PasswordValidException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.PASSWORD_VALID;
    }

    @Override
    public String getMessage() {
        return ResponseCode.PASSWORD_VALID;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
