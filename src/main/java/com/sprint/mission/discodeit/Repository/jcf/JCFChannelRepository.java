package com.sprint.mission.discodeit.Repository.jcf;


import com.sprint.mission.discodeit.Repository.ChannelRepository;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    @Override
    public void save(Channel channel) {

    }

    @Override
    public Channel loadByName(String name) {
        return null;
    }

    @Override
    public Channel loadById(UUID id) {
        return null;
    }

    @Override
    public List<Channel> loadAll() {
        return List.of();
    }

    @Override
    public void update(UUID id, String name) {

    }

    @Override
    public void join(UUID userId, UUID channelId) {

    }

    @Override
    public void leave(UUID userId, UUID channelId) {

    }

    @Override
    public void delete(UUID id) {

    }
}
