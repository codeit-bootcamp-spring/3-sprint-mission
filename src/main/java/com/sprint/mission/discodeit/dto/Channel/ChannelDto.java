package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;


public record ChannelDto(
        UUID id,
        String name,
        String description,
        ChannelType type,
        Instant lastMessageAt,
        List<UserDto> participants
) {

}
