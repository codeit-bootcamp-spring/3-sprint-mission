package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
  private final Map<UUID, User> data = new HashMap<>();

  @Override
  public void save(User user) {
    data.put(user.getId(), user);
  }

  @Override
  public User findById(UUID id) {
    return data.get(id);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID id) {
    data.remove(id);
  }
}
