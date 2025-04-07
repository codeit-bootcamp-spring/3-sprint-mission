package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Channels {
    private final Map<UUID, Channel> channels = new HashMap<>();

    public void add(Channel channel) {
        channels.put(channel.getId(), channel);
    }

    public Channel get(UUID id) {
        return channels.get(id);
    }

    public Channel remove(UUID id) {
        return channels.remove(id);
    }

    public Channel update(UUID id, String channelName) {
        Channel channel = channels.get(id);
        channel.updateChannelName(channelName);
        return channel;
    }

    public Map<UUID, Channel> asReadOnlyMap() {
        return channels;
    }

    public Channel addMessageToChannel(UUID channelId, UUID messageId){
        Channel channel = channels.get(channelId);
        channel.addMessageToChannel(messageId);
        return channel;
    }
}
