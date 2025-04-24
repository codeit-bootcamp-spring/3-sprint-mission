package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> users = new HashMap<>();

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return users.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public List<User> findByNameContains(String name) {
    return users.values().stream()
        .filter(user -> user.getName().contains(name))
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(users.values());
  }

  @Override
  public User save(User user) {
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public void deleteById(UUID id) {
    users.remove(id);
  }
}
