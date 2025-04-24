package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    User loadByName(String name);
    User loadById(UUID id);
    List<User> loadAll();
    void update(UUID id, String name);
    void deleteById(UUID id);
}
