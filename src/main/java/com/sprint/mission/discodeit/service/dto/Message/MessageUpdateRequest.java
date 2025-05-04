package com.sprint.mission.discodeit.service.dto.Message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID id,
        String content
) {
}
