package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record ChannelDTO(

    UUID id,
    String name,
    String description,
    List<UserDTO> participants,
    ChannelType type,
    Instant lastMessageAt
) {

}
