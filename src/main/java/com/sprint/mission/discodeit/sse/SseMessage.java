package com.sprint.mission.discodeit.sse;

import java.util.UUID;

public record SseMessage<T>(
    UUID id,
    String name,
    T data
) {

}
