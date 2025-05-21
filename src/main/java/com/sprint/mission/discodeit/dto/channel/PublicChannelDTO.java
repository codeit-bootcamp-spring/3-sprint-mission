package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public record PublicChannelDTO(String channelName, UUID channelMaster, String description) {

    public static Channel fromDTO(PublicChannelDTO publicChannelDTO) {
        Channel channel = new Channel(publicChannelDTO.channelName(),
                publicChannelDTO.channelMaster,
                publicChannelDTO.description());

        return channel;
    }
}
