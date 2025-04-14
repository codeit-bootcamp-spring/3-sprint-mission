package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;

    public FileChannelService() {
        this.channels = new HashMap<>();
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel newChannel = new Channel(channelName);
        channels.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public Map<UUID, Channel>  readChannels() {
        return channels;
    }

    @Override
    public Channel readChannel(UUID id) {
        return channels.get(id);
    }

    @Override
    public Channel addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = channels.get(channelId);
        channel.addMessageToChannel(messageId);
        return channel;
    }

    @Override
    public Channel addUserToChannel(UUID channelId, UUID userId){
        Channel channel = channels.get(channelId);
        channel.addUserToChannel(userId);
        return channel;
    }

    @Override
    public Channel updateChannel(UUID id, String channelName) {
        Channel channel = channels.get(id);
        channel.updateChannelName(channelName);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return channels.remove(id);
    }
}
