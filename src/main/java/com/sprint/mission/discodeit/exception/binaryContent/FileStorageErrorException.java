package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class FileStorageErrorException extends BinaryContentException {

    public FileStorageErrorException(UUID binaryContentId) {
        super(Instant.now(), ErrorCode.FILE_STORAGE_ERROR, Map.of("binaryContentId", binaryContentId));
    }
}