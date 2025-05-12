package com.sprint.mission.discodeit.dto.channel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDTO(UUID id, Instant createdAt, Instant updatedAt, String channelName,
                                 UUID channelMaster, String description, boolean isPrivate, List<UUID> users,
                                 List<UUID> messages, Instant lastMessageTime) {
}
