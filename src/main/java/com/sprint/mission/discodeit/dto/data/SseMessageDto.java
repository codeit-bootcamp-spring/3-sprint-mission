package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record SseMessageDto(
    UUID id,
    String eventName,
    Object data) {
}