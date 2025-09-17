package com.sprint.mission.discodeit.sse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SseMessage {
    private final UUID id;
    private final String name;
    private final Object data;
    // null 이면 broadcast 의미
    private final Set<UUID> receiverIds;
    private final Instant createdAt;
}
