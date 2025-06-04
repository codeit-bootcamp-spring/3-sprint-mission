package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class InternalErrorException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.INTERNAL_ERROR;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.INTERNAL_SERVER_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
