package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  List<User> findByNameContains(String name);

  List<User> findAll();

  User save(User user);

  void deleteById(UUID id);
}