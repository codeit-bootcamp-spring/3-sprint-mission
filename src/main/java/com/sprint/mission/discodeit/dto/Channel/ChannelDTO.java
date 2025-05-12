package com.sprint.mission.discodeit.dto.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDTO(
        UUID id,
        String channelName,
        String description,
        ChannelType type,
        Instant lastMessageAt,
        List<UUID> participantIds
) {
    public static ChannelDTO from(Channel ch, Instant lastMessageAt, List<UUID> userIdList) {
        return new ChannelDTO(
                ch.getId(),
                ch.getChannelName(),
                ch.getDescription(),
                ch.getType(),
                lastMessageAt,
                userIdList
        );
    }

    /* public 채널 */
    public static ChannelDTO from(Channel ch, Instant lastMessageAt) {
        return from(ch,lastMessageAt,null);
    }
}
