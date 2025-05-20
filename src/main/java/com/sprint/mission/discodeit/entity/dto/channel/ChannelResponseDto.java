package com.sprint.mission.discodeit.entity.dto.channel;

import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelResponseDto {
    private UUID channelId;
    private String name;
    private String description;
    private Instant lastMessageAt;
    private ChannelType type;
    private Set<UUID> memberIds;
}

