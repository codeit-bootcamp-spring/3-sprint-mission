package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.data
 * FileName     : SseMessageDto
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
public record SseMessageDto(
    UUID id,
    String eventName,
    Object data,
    Instant createdAt
) {

}
