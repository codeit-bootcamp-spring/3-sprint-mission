package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ResourceUrlCreationErrorException extends BinaryContentException {

    public ResourceUrlCreationErrorException(UUID binaryContentId) {
        super(Instant.now(), ErrorCode.RESOURCE_URL_CREATION_ERROR, Map.of("binaryContentId", binaryContentId));
    }
}
