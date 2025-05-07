package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record CreatePublicChannelRequest(
//        ChannelType PUBLIC,
        UUID channelId,
        String name,
        String description
) {}
