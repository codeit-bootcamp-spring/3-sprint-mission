package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.errorcode.ErrorCode;
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
