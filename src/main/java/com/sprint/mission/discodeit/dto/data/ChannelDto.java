package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelDto {
  private UUID id;
  private String name;
  private UUID ownerId;
  private ChannelType channelType;
  private List<UUID> memberIds;
  private LocalDateTime lastMessageTime;

  public ChannelDto(UUID id, String name, UUID ownerId, ChannelType channelType, List<UUID> memberIds, LocalDateTime lastMessageTime) {
    this.id = id;
    this.name = name;
    this.ownerId = ownerId;
    this.channelType = channelType;
    this.memberIds = memberIds;
    this.lastMessageTime = lastMessageTime;
  }

  @Override
  public String toString() {
    return
        " name='" + name + '\'' +
        ", channelType=" + channelType +
        ", memberIds=" + memberIds +
        ", lastMessageTime=" + lastMessageTime +
        '}';
  }
}