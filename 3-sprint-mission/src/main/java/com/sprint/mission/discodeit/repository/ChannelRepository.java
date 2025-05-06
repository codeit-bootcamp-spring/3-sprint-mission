package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    Channel save(Channel channel);
    List<Channel> findAll();
    Channel find(UUID id);
    List<Channel> findByName(String name);
    void addEntry(UUID id, UUID userId);
    void delete(UUID id) throws IOException;
}
