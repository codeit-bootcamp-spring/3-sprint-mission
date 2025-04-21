package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, String newUsername);
    User delete(UUID id);
}
