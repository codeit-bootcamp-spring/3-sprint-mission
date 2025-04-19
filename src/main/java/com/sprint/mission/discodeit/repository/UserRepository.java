package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    public User write(User user);

    public User read(UUID userId);

    public List<User> readAll();

    public void delete(UUID userId);
}
