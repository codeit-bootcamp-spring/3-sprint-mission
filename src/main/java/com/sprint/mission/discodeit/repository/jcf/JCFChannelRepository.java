package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelRepository implements ChannelRepository {

    private static volatile JCFChannelRepository instance;
    private final Map<UUID, Channel> channels = new ConcurrentHashMap<>();

    private JCFChannelRepository() {}

    public static JCFChannelRepository getInstance() {
        JCFChannelRepository result = instance;
        if (result == null) {
            synchronized (JCFChannelRepository.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JCFChannelRepository();
                }
            }
        }
        return result;
    }

    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(channels.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public boolean existsById(UUID channelId) {
        return channels.containsKey(channelId);
    }

    @Override
    public void deleteById(UUID channelId) {
        channels.remove(channelId);
    }
}
