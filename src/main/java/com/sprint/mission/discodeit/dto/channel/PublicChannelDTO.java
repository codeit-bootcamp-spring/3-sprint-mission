package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PublicChannelDTO {
    private String channelName;
    private UUID channelMaster;
    private String description;

    public PublicChannelDTO(String channelName, UUID channelMaster, String description) {
        this.channelName = channelName;
        this.channelMaster = channelMaster;
        this.description = description;
    }

    public static Channel toEntity(PublicChannelDTO publicChannelDTO) {
        Channel channel = new Channel();

        channel.updateChannelName(publicChannelDTO.getChannelName());
        channel.updateChannelMaster(publicChannelDTO.getChannelMaster());
        channel.updateDescription(publicChannelDTO.getDescription());
        channel.updatePrivate(false);

        return channel;
    }
}
