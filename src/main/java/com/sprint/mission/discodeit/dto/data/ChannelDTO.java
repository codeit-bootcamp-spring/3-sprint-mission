package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelDTO {
    // 채널 정보
    private UUID channelId;
    private String channelName;
    private ChannelType channelType;
    private String description;
    private Instant lastestMessageAt;
    private List<UUID> participantIds;
}
