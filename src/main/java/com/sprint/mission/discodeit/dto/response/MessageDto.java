package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID channel,
    UUID user,
    String content,
    List<UUID> attachments,
    Instant createdAt,
    Instant updatedAt
) {

}

