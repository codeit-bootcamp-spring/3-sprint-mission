package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileInitFailedException extends BinaryContentException {

    public FileInitFailedException(String path, Throwable cause) {
        super(ErrorCode.FILE_INIT_FAILED, Map.of("path", path, "cause", cause));
    }
}
