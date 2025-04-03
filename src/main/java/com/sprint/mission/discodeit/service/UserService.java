package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    // CRAETE
    void save(User user);
    // READ
    Optional<User> findById(UUID id);
    List<User> findAll();
    // UPDATE
    User update(User user);
    // DELETE
    void deleteById(UUID id);
}
