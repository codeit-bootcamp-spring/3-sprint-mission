package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDTO(UUID id, Instant createdAt, Instant updatedAt, String name,
                                 UUID channelMaster, String description, ChannelType type,
                                 List<UUID> users,
                                 List<UUID> messages, Instant lastMessageTime) {

}
