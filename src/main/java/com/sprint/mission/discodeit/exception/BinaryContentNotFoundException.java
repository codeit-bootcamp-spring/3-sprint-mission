package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import org.springframework.http.HttpStatus;

public class BinaryContentNotFoundException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.BINARY_NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseCode.BINARY_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
