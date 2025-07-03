package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(Object details) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
    }
}