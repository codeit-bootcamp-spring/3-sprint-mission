package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);

    public Optional<User> findById(UUID userId);

    public List<User> findAll();

    public boolean existsById(UUID id);

    public void deleteById(UUID userId);
}
