package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);

    Map<UUID, User> load();

    void deleteUser(UUID id);

}
