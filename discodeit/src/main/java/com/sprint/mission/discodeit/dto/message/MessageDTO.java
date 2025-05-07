package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDTO(UUID id,
                         Instant cratedAt,
                         Instant updatedAt,
                         String content,
                         UUID authorId,
                         UUID channelId,
                         List<UUID> attachmentIds) {
}
