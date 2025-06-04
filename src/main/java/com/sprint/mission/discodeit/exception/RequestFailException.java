package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class RequestFailException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.REQUEST_FAIL;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.REQUEST_FAIL;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
