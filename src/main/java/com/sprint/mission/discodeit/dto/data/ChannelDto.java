package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    String name,
    UUID ownerId,
    ChannelType channelType,
    List<UUID> memberIds,
    LocalDateTime lastMessageTime
) {
}
