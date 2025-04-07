package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String name);
    User getUser(UUID id);
    List<User> getAllUsers();
    User getUserByName(String name);
    void updateUser(UUID id, String name);
    void deleteUser(UUID id);
    boolean existsById(UUID id);
}
