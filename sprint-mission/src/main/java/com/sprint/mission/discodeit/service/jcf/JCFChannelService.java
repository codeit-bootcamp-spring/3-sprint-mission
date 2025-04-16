package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel newChannel = new Channel(channelName);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public Map<UUID, Channel>  readChannels() {
        return data;
    }

    @Override
    public Channel readChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public Channel addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = data.get(channelId);
        channel.addMessageToChannel(messageId);
        return channel;
    }

    @Override
    public Channel addUserToChannel(UUID channelId, UUID userId){
        Channel channel = data.get(channelId);
        channel.addUserToChannel(userId);
        return channel;
    }

    @Override
    public Channel updateChannel(UUID id, String channelName) {
        Channel channel = data.get(id);
        channel.updateChannelName(channelName);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return data.remove(id);
    }
}
