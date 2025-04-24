package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);
    public List<User> read();
    public Optional<User> readById(UUID id);
    public void update(UUID id, User user);
    public void delete(User user);
    public Boolean duplicateCheck(User user);
}
