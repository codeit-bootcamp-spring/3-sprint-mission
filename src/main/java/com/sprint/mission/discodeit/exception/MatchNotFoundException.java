package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.errorcode.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchNotFoundException extends RuntimeException {

    ErrorCode errorCode;

    public MatchNotFoundException(String message) {
        super(message);
    }
}
