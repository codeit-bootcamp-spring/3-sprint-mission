package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus, UUID> implements UserStatusRepository {

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    super(Paths.get(System.getProperty("user.dir"), fileDirectory, "userStatus.ser").toString());
  }

  @Override
  public UserStatus save(UserStatus status) {
    return super.save(status, status.getId());
  }

  @Override
  public Optional<UserStatus> find(UUID id) {
    return super.findById(id);
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(status -> status.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return super.findAll();
  }

  @Override
  public void delete(UUID id) {
    super.delete(id);
  }
}

