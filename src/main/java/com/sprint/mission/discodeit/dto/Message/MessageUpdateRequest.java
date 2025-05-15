package com.sprint.mission.discodeit.dto.Message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID id,
        String content
) {
}
