package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReadStatusResponse(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {

}
