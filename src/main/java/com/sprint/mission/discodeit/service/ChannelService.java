package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    public Channel createChannel(ChannelCreateDto channelCreateDto);

    public Channel createChannel(ChannelCreatePrivateDto channelCreatePrivateDto);

    public Optional<ChannelResponseDto> getChannel (GetPublicChannelRequestDto getPublicChannelRequestDto);

    public Optional<ChannelResponseDto> getChannel (GetPrivateChannelRequestDto getPrivateChannelRequestDto);

    public List<ChannelResponseDto> findAllByUserId (UUID userId);

    public boolean deleteChannel (UUID id, User user);

    public boolean modifyChannel (ChannelUpdateRequestDto channelUpdateRequestDto);

    public boolean kickOutChannel (UUID channelId, User kickUser, User admin);

    public boolean joinChannel(Channel channel, User user);

}
