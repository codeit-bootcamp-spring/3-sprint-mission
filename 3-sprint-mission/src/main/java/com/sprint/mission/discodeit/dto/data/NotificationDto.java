package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receivedId,
    String title,
    String content
) {

}
