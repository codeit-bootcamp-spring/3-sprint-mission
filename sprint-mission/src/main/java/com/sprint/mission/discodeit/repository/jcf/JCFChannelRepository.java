package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels;

    public JCFChannelRepository() {
        this.channels = new HashMap<>();
    }


    @Override
    public void save(Channel channel){
        channels.put(channel.getId(),channel);
    }

    @Override
    public Map<UUID, Channel> load() {
        return channels;
    }

    @Override
    public void deleteChannel(UUID id){
        channels.remove(id);
    }
}
