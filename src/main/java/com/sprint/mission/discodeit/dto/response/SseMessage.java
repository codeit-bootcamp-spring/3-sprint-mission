package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record SseMessage<T>(
    UUID id,
    String name,
    T data
) {

}
