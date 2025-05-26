package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    List<User> findAll();
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void deleteById(UUID id);
}
