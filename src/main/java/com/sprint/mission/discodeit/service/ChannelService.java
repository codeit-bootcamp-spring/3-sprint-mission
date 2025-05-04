package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublicChannel(PublicChannelCreateRequest request);
    ChannelDto createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelDto findById(UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(PublicChannelUpdateRequest request);
    void deleteById(UUID channelId);

//    Channel createChannel(Channel channel);
//    Optional<Channel> getChannel(UUID channelId);
//    List<Channel> getAllChannels();
//    void updateChannel(Channel channel);
//    void deleteChannel(UUID channelId);
}