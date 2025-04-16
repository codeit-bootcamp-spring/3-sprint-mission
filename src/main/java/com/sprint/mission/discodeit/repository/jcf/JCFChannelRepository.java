package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel write(Channel channel) {
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public boolean delete(UUID id) {
        this.data.remove(id);
        return true;
    }
}
