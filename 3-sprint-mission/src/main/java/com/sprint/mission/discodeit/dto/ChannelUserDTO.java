package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ChannelUserDTO(
        UUID userId,
        UUID channelId
) {
}
