package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;


@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> data; //database

  public JCFUserRepository() {
    this.data = new HashMap<>();
  }

  @Override
  public User save(User user) {
    this.data.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(UUID userId) {
    return Optional.ofNullable(this.data.get(userId));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return this.findAll().stream().filter((user) -> user.getUsername().equals(username))
        .findFirst();
  }

  @Override
  public Optional<User> findByEmail(String userEmail) {
    return this.findAll().stream().filter((user) -> user.getEmail().equals(userEmail)).findFirst();
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(this.data.values());
  }

  @Override
  public boolean existsById(UUID userId) {
    return this.data.containsKey(userId);
  }

  @Override
  public void deleteById(UUID userId) {
    this.data.remove(userId);
  }

  @Override
  public boolean existsByEmail(String email) {
    return this.findAll().stream().anyMatch((user) -> user.getEmail().equals(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    return this.findAll().stream().anyMatch((user) -> user.getUsername().equals(username));
  }

}
