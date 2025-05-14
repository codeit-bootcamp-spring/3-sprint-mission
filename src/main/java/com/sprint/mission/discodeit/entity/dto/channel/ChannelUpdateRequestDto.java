package com.sprint.mission.discodeit.entity.dto.channel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class ChannelUpdateRequestDto {
    private UUID channelId;
    private UUID adminId;
    private String channelName;
    private String channelDescription;
}
