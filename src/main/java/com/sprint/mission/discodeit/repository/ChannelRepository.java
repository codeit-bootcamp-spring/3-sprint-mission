package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void save(Channel channel);

    void saveAll(List<Channel> channels);

    List<Channel> loadAll();

    Channel loadById(UUID id);

    List<Channel> loadByName(String name);

    List<Channel> loadByType(String type);
}
