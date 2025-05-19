package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        String name,
        String description,
        ChannelType type,
        Instant lastMessageAt,
        List<UUID> participantIds
) {

    public static ChannelDto from(Channel ch, Instant lastMessageAt, List<UUID> userIdList) {
        return new ChannelDto(
                ch.getId(),
                ch.getName(),
                ch.getDescription(),
                ch.getType(),
                lastMessageAt,
                userIdList
        );
    }

    /* public 채널 */
    public static ChannelDto from(Channel ch, Instant lastMessageAt) {
        return from(ch, lastMessageAt, null);
    }
}
