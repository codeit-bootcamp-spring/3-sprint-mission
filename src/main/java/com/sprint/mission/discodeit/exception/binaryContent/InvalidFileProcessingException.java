package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidFileProcessingException extends BinaryContentException {

    public InvalidFileProcessingException(String fileName) {
        super(ErrorCode.FILE_PROCESSING_FAILED, Map.of("fileName", fileName));
    }
}
