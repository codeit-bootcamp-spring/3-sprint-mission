package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FileioUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(value = "repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository implements UserStatusRepository {

  private Map<String, UserStatus> userStatusData;
  private Path path;

  public FileUserStatusRepository(@Value("repository.user-status-file-path") Path path) {
    this.path = path;
    if (!Files.exists(this.path)) {
      try {
        Files.createFile(this.path);
        FileioUtil.save(this.path, new HashMap<>());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    FileioUtil.init(this.path);
    this.userStatusData = FileioUtil.load(this.path, UserStatus.class); // 변경된 메서드로 파일 로딩하는 파트
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    userStatusData.put(userStatus.getId().toString(), userStatus);
    FileioUtil.save(path, userStatusData);
    return userStatus;
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return Optional.ofNullable(userStatusData.get(id.toString()));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return userStatusData.values().stream()
        .filter(userStatus -> userStatus.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusData.values().stream().toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return userStatusData.containsKey(id.toString());
  }

  @Override
  public void deleteById(UUID id) {
    userStatusData.remove(id.toString());
    FileioUtil.save(this.path, userStatusData);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    userStatusData.values().removeIf(userStatus -> userStatus.getUserId().equals(userId));
    FileioUtil.save(this.path, userStatusData);
  }
}