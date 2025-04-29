package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap = new HashMap<>();

    @Override
    public void save(Channel channel) {
        channelMap.put(channel.getChannelId(), channel);
    }

    @Override
    public void saveAll(List<Channel> channels) {
        channelMap.clear();
        for (Channel channel : channels) {
            channelMap.put(channel.getChannelId(), channel);
        }
    }

    @Override
    public List<Channel> loadAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel loadById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> loadByName(String name) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getChannelName().equals(name)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> loadByType(String type) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getChannelType().equals(type)) {
                result.add(channel);
            }
        }
        return result;
    }
}
