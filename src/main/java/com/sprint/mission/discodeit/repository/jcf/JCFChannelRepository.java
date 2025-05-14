package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);

        return channel;
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
    public List<Channel> findByPrivateChannelUserId(UUID userId) {
        return data.values().stream()
                .filter(channel -> channel.getUsers().contains(userId) && channel.isPrivate())
                .toList();
    }

    @Override
    public List<Channel> findByNameContaining(String name) {
        return data.values().stream()
                .filter(channel -> !channel.isPrivate() && channel.getChannelName().contains(name))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID channelId) {
        data.remove(channelId);
    }
}
