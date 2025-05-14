package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelDTO {
    private UUID channelMaster;
    private List<UUID> users;

    public PrivateChannelDTO(UUID channelMaster, List<UUID> users) {
        this.channelMaster = channelMaster;
        this.users = users;
    }

    public static Channel toEntity(PrivateChannelDTO privateChannelDTO) {
        Channel channel = new Channel();

        channel.updateChannelMaster(privateChannelDTO.getChannelMaster());
        // privateChannel의 userList 추가
        privateChannelDTO.getUsers().forEach(channel::updateUsers);
        channel.updatePrivate(true);
        return channel;
    }
}
