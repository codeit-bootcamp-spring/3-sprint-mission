package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    public Channel createChannel(Channel channel);

    public Optional<Channel> getChannel (GetPublicChannelRequestDto getPublicChannelRequestDto);

    public Optional<Channel> getChannel (GetPrivateChannelRequestDto getPrivateChannelRequestDto);

    public List<Channel> findAllByUserId (UUID userId);

    public boolean deleteChannel (UUID id, UUID userId);

    public boolean modifyChannel (Channel channel);

    public boolean kickOutChannel (UUID channelId, User kickUser, User admin);

    public boolean joinChannel(UUID channelId, User user);

}
