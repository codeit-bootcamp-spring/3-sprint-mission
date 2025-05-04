package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

//    private final Map<UUID, Channel> data = new HashMap<>();
//
//    public Channel save(Channel channel) {
//        return data.put(channel.getId(), channel);
//    }
//
//    public Optional<Channel> findById(UUID channelId) {
//        return Optional.ofNullable(data.get(channelId));
//    }
//
//    public List<Channel> findAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    public void deleteById(UUID channelId) {
//        data.remove(channelId);
//    }
}