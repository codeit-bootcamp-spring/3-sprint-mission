package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID receiverId,
    Instant createdAt,
    String title,
    String content
) { }
