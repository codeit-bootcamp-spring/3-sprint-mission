package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> users = new HashMap<>();

  @Override
  public void insert(User user) {
    if (users.containsKey(user.getId())) {
      throw new IllegalArgumentException("이미 존재하는 사용자입니다. [ID: " + user.getId() + "]");
    }
    users.put(user.getId(), user);
  }

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
  public Optional<User> findByName(String name) {
    return users.values().stream()
        .filter(user -> user.getName().equals(name))
        .findFirst();
  }

  @Override
  public Optional<User> findByNameAndPassword(String name, String password) {
    return users.values().stream()
        .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
        .findFirst();
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
  public void update(User user) {
    if (!users.containsKey(user.getId())) {
      throw new IllegalArgumentException("존재하지 않는 사용자입니다. [ID: " + user.getId() + "]");
    }
    users.put(user.getId(), user);
  }

  @Override
  public void delete(UUID id) {
    users.remove(id);
  }
}
