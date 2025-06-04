package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class DuplicateReadStatusException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.DUPLICATE_READ_STATUS;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.DUPLICATE_READ_STATUS;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
