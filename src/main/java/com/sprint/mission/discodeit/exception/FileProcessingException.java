package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import org.springframework.http.HttpStatus;

public class FileProcessingException extends BaseException {

    @Override
    public String getCode() {
        return ResponseCode.FILE_PROCESSING_ERROR;
    }

    @Override
    public String getMessage() {
        return ResponseMessage.FILE_PROCESSING_ERROR;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
