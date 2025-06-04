package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException{
    @Override
    public String getCode() {
        return ResponseCode.USER_NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.USER_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
