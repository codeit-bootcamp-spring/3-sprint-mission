package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    Channel save(Channel channel);
    List<Channel> findAll();
    Optional<Channel> findById(UUID id);
    List<Channel> findByName(String name);
    boolean existsById(UUID id);
    boolean existsByName(String name);
    void deleteById(UUID id);
}
