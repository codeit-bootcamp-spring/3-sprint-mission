package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    public void write(User user);

    public User read(UUID id);

    public List<User> readAll();

    public boolean delete(UUID id);
}
