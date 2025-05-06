package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PrivateChannelResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    String description,
    Channel.ChannelType type,
    UUID creatorId,
    List<UUID> participantIds,
    Instant latestMessageTime
) implements ChannelResponse {

  public static PrivateChannelResponse from(Channel channel, Instant latestMessageTime) {
    return new PrivateChannelResponse(
        channel.getId(),
        channel.getCreatedAt(),
        channel.getUpdatedAt(),
        channel.getName(),
        channel.getDescription(),
        channel.getType(),
        channel.getCreatorId(),
        channel.getParticipants(),
        latestMessageTime
    );
  }
}
