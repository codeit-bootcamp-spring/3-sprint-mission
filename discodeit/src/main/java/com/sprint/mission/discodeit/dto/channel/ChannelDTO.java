package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChannelDTO(UUID id,
                         String name,
                         ChannelType type,
                         String description,
                         List<UUID> participantIds,
                         Instant lastMessageAt) {

}