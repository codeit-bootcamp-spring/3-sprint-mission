package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(User user);
    User getUser(UUID userId);
    List<User> getAllUsers();
    void updateUser(UUID userId, String userName);
    void deleteUser(UUID userId);
}
