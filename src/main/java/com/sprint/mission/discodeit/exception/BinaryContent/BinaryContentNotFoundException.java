package com.sprint.mission.discodeit.exception.BinaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContentNotFoundException extends BinaryContentException {

    private final UUID binaryContentId;

    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(
                ErrorCode.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId)
        );
        this.binaryContentId = binaryContentId;
    }
}
