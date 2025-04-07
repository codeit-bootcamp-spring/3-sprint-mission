package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.Channels;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Channels channels = new Channels();

    @Override
    public Channel createChannel(String channelName) {
        Channel newChannel = new Channel(channelName);
        channels.add(newChannel);
        return newChannel;
    }

    @Override
    public Map<UUID, Channel>  readChannels() {
        return channels.asReadOnlyMap();
    }

    @Override
    public Channel readChannel(UUID id) {
        return channels.get(id);
    }

    @Override
    public Channel addMessageToChannel(UUID channelId, UUID messageId) {
        return channels.addMessageToChannel(channelId, messageId);
    }

    @Override
    public Channel updateChannel(UUID id, String channelName) {
        return channels.update(id, channelName);
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return channels.remove(id);
    }
}
