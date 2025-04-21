package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(User user);

    User getById(UUID id);

    List<User> getAll();

    User update(User user);

    void delete(UUID id);
}
