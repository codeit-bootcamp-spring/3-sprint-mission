package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository extends AbstractFileRepository<User, UUID> implements UserRepository {
  private static final String FILE_PATH = "users.ser";

  public FileUserRepository() {
    super(FILE_PATH);
  }

  @Override
  public User save(User user) {
    super.save(user, user.getId());
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return super.findById(id);
  }

  @Override
  public List<User> findAll() {
    return super.findAll();
  }

  @Override
  public User update(User user) {
    super.update(user, user.getId());
    return user;
  }

  @Override
  public void delete(UUID id) {
    super.delete(id);
  }
}

