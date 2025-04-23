package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository)
    {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String channelName){

        Channel channel = new Channel(channelName);
        channelRepository.save(channel);
        return channel;

    }

    @Override
    public Optional<Channel> readChannel(UUID id){
        return Optional.ofNullable(channelRepository.load().get(id));
    }

    @Override
    public Map<UUID, Channel> readChannels() {
        return channelRepository.load();
    }

    @Override
    public Channel addMessageToChannel(UUID channelId, UUID messageId)
    {
        Channel channel = channelRepository.load().get(channelId);
        channel.addMessageToChannel(messageId);
        channelRepository.save(channel);
        return channel;

    }

    @Override
    public Channel addUserToChannel(UUID channelId, UUID userId)
    {
        Channel channel = channelRepository.load().get(channelId);
        channel.addUserToChannel(userId);
        channelRepository.save(channel);
        return channel;

    }

    @Override
    public Channel updateChannel(UUID id, String channelName)
    {
        Channel channel = channelRepository.load().get(id);
        channel.updateChannelName(channelName);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID id)
    {
        channelRepository.deleteChannel(id);
        Channel channel = channelRepository.load().get(id);
        channelRepository.save(channel);
        return channel;
    }

}
