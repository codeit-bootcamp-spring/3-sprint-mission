package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID messageId,
        String newContent
) {
}
