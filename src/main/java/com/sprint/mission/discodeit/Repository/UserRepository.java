package com.sprint.mission.discodeit.Repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void init();
    void save(User user);
    User loadByIndex(String name);
    User loadById(UUID id);
    List<User> loadAll();
    void deleteById(UUID id);
}
