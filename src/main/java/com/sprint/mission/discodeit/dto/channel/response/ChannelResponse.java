package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.Dto.channel
 * fileName       : ChannelCreateResponse
 * author         : doungukkim
 * date           : 2025. 4. 29.
 */
@Getter
@Builder
public class ChannelResponse {
  private final UUID id;
  private final ChannelType type;
  private final String name;
  private final String description;
  private final List<UserResponse> participants;
  private final Instant lastMessageAt;
}
