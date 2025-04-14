package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels= new HashMap<>();


    @Override
    public void save(Channel channel){
        channels.put(channel.getId(),channel);
    }

    @Override
    public Map<UUID, Channel> readChannels() {
        return channels;
    }

    @Override
    public Channel readChannel(UUID id){
        return channels.get(id);
    }

    @Override
    public void deleteChannel(UUID id){
        channels.remove(id);
    }
}
