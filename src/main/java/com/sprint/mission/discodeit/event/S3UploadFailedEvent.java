package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record S3UploadFailedEvent (
        UUID userId,
        UUID binaryContentId,
        String fileName

){
}
