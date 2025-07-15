package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentInvalidException extends BinaryContentException {

    public BinaryContentInvalidException() {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }

    public static BinaryContentInvalidException withFile(String fileName) {
        BinaryContentInvalidException exception = new BinaryContentInvalidException();
        exception.addDetail("fileName", fileName);
        return exception;
    }

    public static BinaryContentInvalidException missingFile() {
        BinaryContentInvalidException exception = new BinaryContentInvalidException();
        exception.addDetail("fileName", "missing or null");
        return exception;
    }
} 