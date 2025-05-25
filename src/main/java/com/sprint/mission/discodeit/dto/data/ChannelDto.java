package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelDto(
    UUID channelId,
    ChannelType channelType,
    String channelName,
    String description,
    List<UUID> participantIds,
    LocalDateTime lastMessageAt) {
}