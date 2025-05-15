package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelDTO(UUID id,
                         Instant createdAt,
                         String channelName,
                         ChannelType type,
                         String description,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {


  public static ChannelDTO fromDomain(Channel channel, Instant lastMessageAt) {
    return ChannelDTO.builder()
        .id(channel.getId())
        .type(channel.getType())
        .channelName(channel.getChannelName())
        .description(channel.getDescription())
        .participantIds(channel.getParicipantIds())
        .lastMessageAt(lastMessageAt)
        .build();
  }
}