package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.UUID;

public record PublicChannelResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    String description,
    Channel.ChannelType type,
    UUID creatorId,
    Instant latestMessageTime
) implements ChannelResponse {

  public static PublicChannelResponse from(Channel channel, Instant latestMessageTime) {
    return new PublicChannelResponse(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getName(),
        channel.getDescription(),
        channel.getType(),
        channel.getCreatorId(),
        latestMessageTime
    );
  }
}
