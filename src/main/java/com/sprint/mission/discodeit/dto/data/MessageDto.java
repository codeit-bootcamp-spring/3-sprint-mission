package com.sprint.mission.discodeit.dto.data;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public record MessageDto(
    UUID messageId,
    String content,
    UUID authorId,
    UUID channelId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<UUID> attachmentIds) {
}
