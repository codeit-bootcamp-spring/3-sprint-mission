package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface UserRepository {
    void save(User user);

    Map<UUID, User> readUsers();

    User readUser(UUID id);

    void deleteUser(UUID id);

}
