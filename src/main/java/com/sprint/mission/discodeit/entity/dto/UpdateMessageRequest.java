package com.sprint.mission.discodeit.entity.dto;

import java.util.UUID;

public record UpdateMessageRequest(
        UUID messageId,
        String content
) {
}
