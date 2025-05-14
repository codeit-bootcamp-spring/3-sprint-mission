package com.sprint.mission.discodeit.entity.dto.channel;

import com.sprint.mission.discodeit.entity.User;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChannelResponseDto {
    private UUID channelId;
    private String name;
    private String description;
    private User admin;
    private Instant lastMessageAt;
    private ChannelType type;
    private Set<UUID> memberIds;
}

