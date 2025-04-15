package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username);
    User findById(UUID id);
    List<User> findAll();
    void update(UUID id, String newUsername);
    void delete(UUID id);
}
