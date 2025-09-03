package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

public record MessageCreatedEvent (
        MessageDto data,
        Instant createdAt
){
}
