package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    public Optional<Channel> getChannel(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    public void updateChannel(UUID channelId, String channelName) {
        getChannel(channelId).ifPresent(channel -> channel.updateChannelName(channelName));
    }

    public void deleteChannel(UUID channelId) {
        data.remove(channelId);
    }

    @Override
    public void addMessageId(UUID channelId, UUID messageId) {
        getChannel(channelId).ifPresent(channel -> channel.addMessage(messageId));
    }

    @Override
    public void addUserId(UUID channelId, UUID userId) {
        getChannel(channelId).ifPresent(channel -> channel.addUser(userId));
    }
}
