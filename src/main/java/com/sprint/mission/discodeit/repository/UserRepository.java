package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    User loadByName(String name);
    User loadByEmail(String email);
    User loadById(UUID id);
    List<User> loadAll();
    void deleteById(UUID id);
}
