package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.util.UUID;

public record S3UploadFailedEvent(
    UUID binaryContentId,
    String requestId,
    String errorMessage
) {

    public static S3UploadFailedEvent from(BinaryContentDto meta, Throwable cause,
        String requestId) {
        return new S3UploadFailedEvent(
            meta.id(),
            requestId,
            cause.getMessage()
        );
    }

}
