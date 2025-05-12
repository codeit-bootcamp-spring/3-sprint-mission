package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDTO(UUID id, Instant createdAt, Instant updatedAt, String channelName,
                                 UUID channelMaster, String description, boolean isPrivate, List<UUID> users,
                                 List<UUID> messages, Instant lastMessageTime) {
}
