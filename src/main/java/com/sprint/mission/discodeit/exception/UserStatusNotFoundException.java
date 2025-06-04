package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class UserStatusNotFoundException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.USER_STATUS_NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.USER_STATUS_NOT_FOUND;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
