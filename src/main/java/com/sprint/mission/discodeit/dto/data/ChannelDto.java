package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import java.time.Instant;
import java.util.List;
import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelDto(
        UUID channelId,
        ChannelType channelType,
        String channelName,
        String password,
        List<UUID> participantIds,
        Instant lastMessageAt
) {
}
