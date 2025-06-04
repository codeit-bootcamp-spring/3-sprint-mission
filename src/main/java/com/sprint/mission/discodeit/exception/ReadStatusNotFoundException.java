package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class ReadStatusNotFoundException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.READ_STATUS_NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.READ_STATUS_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
