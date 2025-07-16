package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class DuplicateFileException extends BinaryContentException {

    public DuplicateFileException(UUID binaryContentId, String fileName) {
        super(ErrorCode.DUPLICATE_FILE,
            Map.of("binaryContentId", binaryContentId, "fileName", fileName));
    }

}
