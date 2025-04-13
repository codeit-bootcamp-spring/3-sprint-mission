package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(User user); // C
    User readUser(UUID id); // R
    List<User> readAllUsers();
    void updateUser(UUID id, String newName); // U
    void deleteUser(UUID id); // D
}
