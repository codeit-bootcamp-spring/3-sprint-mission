package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    public Channel createChannel(ChannelCreateDto channelCreateDto);

    public Channel createChannel(ChannelCreatePrivateDto channelCreatePrivateDto);

    public Optional<Channel> getChannel (GetPublicChannelRequestDto getPublicChannelRequestDto);

    public Optional<Channel> getChannel (GetPrivateChannelRequestDto getPrivateChannelRequestDto);

    public List<Channel> findAllByUserId (UUID userId);

    public boolean deleteChannel (UUID id, User user);

    public boolean modifyChannel (ChannelUpdateRequestDto channelUpdateRequestDto);

    public boolean kickOutChannel (UUID channelId, User kickUser, User admin);

    public boolean joinChannel(Channel channel, User user);

}
