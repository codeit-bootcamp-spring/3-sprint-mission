package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublicChannelCreateRequest {
    private String channelName;
    private ChannelType channelType;  // PUBLIC CHANNEL
    private String description;
}
