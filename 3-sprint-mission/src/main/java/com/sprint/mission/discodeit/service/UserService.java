package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    void create(User user);

    List<User> readById(UUID id);

    List<User> readAll();

    void update(UUID id, User user);

    void deleteById(UUID id);

}
