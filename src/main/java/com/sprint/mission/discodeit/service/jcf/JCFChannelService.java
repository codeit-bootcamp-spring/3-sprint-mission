package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Optional<Channel> foundChannel = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(channelId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundChannel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public void deleteById(UUID channelId) {
        data.remove(channelId);
    }
}
