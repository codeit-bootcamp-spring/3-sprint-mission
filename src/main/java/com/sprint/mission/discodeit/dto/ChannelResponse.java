package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel.ChannelType;
import java.time.Instant;
import java.util.UUID;

public interface ChannelResponse {

  UUID id();

  Instant createdAt();

  Instant updatedAt();

  String name();

  String description();

  ChannelType type();

  UUID creatorId();

  Instant latestMessageTime();
}
