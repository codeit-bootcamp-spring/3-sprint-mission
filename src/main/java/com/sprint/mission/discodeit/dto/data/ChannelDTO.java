package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record ChannelDTO(
    UUID id,
    ChannelType type,
    String name,
    String description,
    Instant lastMessageTime,
    List<UUID> participantIds
) {

}