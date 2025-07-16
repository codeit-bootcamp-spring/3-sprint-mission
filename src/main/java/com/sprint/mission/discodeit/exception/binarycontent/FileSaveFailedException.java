package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileSaveFailedException extends BinaryContentException {

    public FileSaveFailedException(String fileName, Throwable cause) {
        super(ErrorCode.FILE_SAVE_FAILED, Map.of("fileName", fileName, "cause", cause));
    }

}
