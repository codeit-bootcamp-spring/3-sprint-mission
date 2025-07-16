package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.entity.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;

public class BinaryContentException extends DiscodeitException {

    public BinaryContentException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message, errorCode, details);
    }
}
