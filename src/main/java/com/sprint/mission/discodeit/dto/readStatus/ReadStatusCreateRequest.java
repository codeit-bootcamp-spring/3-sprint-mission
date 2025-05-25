package com.sprint.mission.discodeit.dto.readStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReadStatusCreateRequest(
                UUID userId,
                UUID channelId,
                LocalDateTime lastReadAt) {
}
