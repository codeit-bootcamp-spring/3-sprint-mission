package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelResponse(
    UUID id,
    Channel.ChannelType type,
    String name,
    String description,
    List<UserResponse> participants,
    Instant lastMessageAt
) {

}
