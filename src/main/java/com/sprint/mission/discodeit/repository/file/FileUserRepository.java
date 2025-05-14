package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository extends AbstractFileRepository<User, UUID> implements UserRepository {
  //private static final String FILE_PATH = "users.ser";

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    super(Paths.get(System.getProperty("user.dir"), fileDirectory, "user.ser").toString());
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
  public Optional<User> findByUsername(String username) {
    return super.findAll().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst();
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

