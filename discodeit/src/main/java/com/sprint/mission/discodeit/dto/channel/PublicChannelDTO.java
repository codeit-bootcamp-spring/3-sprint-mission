package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PublicChannelDTO(UUID id,
                               Instant createdAt,
                               String name,
                               ChannelType type,
                               String description,
                               List<UUID> participantIds,
                               Instant lastMessageAt) {

  public static PublicChannelDTO fromDomain(Channel channel, Instant lastMessageAt) {
    return PublicChannelDTO.builder()
        .id(channel.getId())
        .createdAt(channel.getCreatedAt())
        .name(channel.getName())
        .type(channel.getType())
        .description(channel.getDescription())
        .lastMessageAt(lastMessageAt)
        .build();
  }
}
