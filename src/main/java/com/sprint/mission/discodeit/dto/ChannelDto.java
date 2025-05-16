package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID channelId,
    ChannelType type,
    String channelName,
    List<String> categories,
    List<UUID> memberIds,
    Instant lastMessageAt
) {

}
