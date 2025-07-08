package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileUploadInvalidException extends BinaryContentException {

    public FileUploadInvalidException(String fileName) {
        super(ErrorCode.FILE_UPLOAD_INVALID, Map.of("fileName", fileName));
    }

}
