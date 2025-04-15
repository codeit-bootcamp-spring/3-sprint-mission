package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String RRN, String name, int age, String email); // C
    User readUser(UUID id); // R
    List<User> readAllUsers();
    User updateUser(UUID id, String newName, String newEmail); // U
    void deleteUser(UUID id); // D
}
