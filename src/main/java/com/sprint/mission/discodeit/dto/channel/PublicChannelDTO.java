package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public record PublicChannelDTO(String channelName, UUID channelMaster, String description) {

    public static Channel toEntity(PublicChannelDTO publicChannelDTO) {
        Channel channel = new Channel();

        channel.updateChannelName(publicChannelDTO.channelName());
        channel.updateChannelMaster(publicChannelDTO.channelMaster());
        channel.updateDescription(publicChannelDTO.description());
        channel.updatePrivate(false);

        return channel;
    }
}
