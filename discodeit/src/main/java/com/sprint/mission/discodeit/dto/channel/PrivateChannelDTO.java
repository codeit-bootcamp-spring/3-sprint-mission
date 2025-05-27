package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PrivateChannelDTO(
    UUID id,
    Instant createAt,
    ChannelType type,
    List<UUID> participantIds,
    Instant lastMessageAt) {

  public static PrivateChannelDTO fromDomain(Channel channel, Instant lastMessageAt) {
    return PrivateChannelDTO.builder()
        .id(channel.getId())
        .createAt(channel.getCreatedAt())
        .type(channel.getType())
        .lastMessageAt(lastMessageAt)
        .build();

  }
}
