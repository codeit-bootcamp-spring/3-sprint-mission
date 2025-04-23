package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels;

    public JCFChannelRepository() {
        channels = new HashMap<UUID, Channel>();
    }

    @Override
    public Map<UUID, Channel> loadFromFile() {
        return channels;
    }

    @Override
    public void saveToFile(Map<UUID, Channel> channels) {
        this.channels.putAll(channels);
    }
}
