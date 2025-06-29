package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentException extends DiscodeitException {
    public BinaryContentException(ErrorCode code, Object details) {
        super(code, details);
    }
}
