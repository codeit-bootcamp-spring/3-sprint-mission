package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    // 저장 로직
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel createChannel(Channel channel) {
        // 저장 로직
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(UUID channelId) {
        // 저장 로직
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> getAllChannels() {
        // 저장 로직
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateChannel(Channel channel) {
        // 저장 로직
        data.put(channel.getId(), channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        // 저장 로직
        data.remove(channelId);
    }
}
