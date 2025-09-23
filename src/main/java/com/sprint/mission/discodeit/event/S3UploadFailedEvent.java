package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record S3UploadFailedEvent(
        String requestId,
        UUID binaryContentId,
        String errorMessage,
        List<User> users
) {
}
