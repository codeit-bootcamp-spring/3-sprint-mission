package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusException extends DiscodeitException {

    public ReadStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

}