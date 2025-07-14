package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(

    UUID id,
    String name,
    String description,
    List<UserDto> participants,
    ChannelType type,
    Instant lastMessageAt
) {

}
