package com.sprint.mission.discodeit.controller.exception;

import com.sprint.mission.discodeit.controller.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidArgException extends RuntimeException {

    ErrorCode errorCode;
    
    public InvalidArgException(String message) {
        super(message);
    }
}
