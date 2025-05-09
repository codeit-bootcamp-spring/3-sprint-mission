package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public record PrivateChannelDTO(UUID channelMaster, List<UUID> users) {

    public static Channel toEntity(PrivateChannelDTO privateChannelDTO) {
        Channel channel = new Channel();

        channel.updateChannelMaster(privateChannelDTO.channelMaster());
        // privateChannel의 userList 추가
        privateChannelDTO.users().forEach(channel::updateUsers);
        channel.updatePrivate(true);
        return channel;
    }
}
