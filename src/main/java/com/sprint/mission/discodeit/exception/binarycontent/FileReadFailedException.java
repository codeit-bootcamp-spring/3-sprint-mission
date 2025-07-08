package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileReadFailedException extends BinaryContentException {

    public FileReadFailedException(String path, Throwable cause) {
        super(ErrorCode.FILE_READ_FAILED, Map.of("path", path, "cause", cause));

    }

}
