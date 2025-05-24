package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileSaveManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

  @Value("${discodeit.repository.file-directory}")
  private String FILE_DIRECTORY;
  private final String FILENAME = "userRepo.ser";
  private final Map<UUID, User> data = new ConcurrentHashMap<>();

  // 동시성 이슈를 제어하기 위해 파일은 한번만 로드
  @PostConstruct
  public void init() {
    Map<UUID, User> loaded = FileSaveManager.loadFromFile(getFile());
    if (loaded != null) {
      data.putAll(loaded);
    }
  }

  private File getFile() {
    return new File(FILE_DIRECTORY, FILENAME);
  }

  @Override
  public User save(User user) {
    data.put(user.getId(), user);

    FileSaveManager.saveToFile(getFile(), data);

    return user;
  }

  @Override
  public Optional<User> findById(UUID userId) {
    Optional<User> foundUser = data.entrySet().stream()
        .filter(entry -> entry.getKey().equals(userId))
        .map(Map.Entry::getValue)
        .findFirst();

    return foundUser;
  }

  @Override
  public List<User> findByNameContaining(String name) {
    return data.values().stream()
        .filter(user -> user.getUsername().contains(name))
        .toList();
  }

  @Override
  public Optional<User> findByName(String name) {
    Optional<User> foundUser = data.values().stream()
        .filter(user -> user.getUsername().equals(name))
        .findFirst();

    return foundUser;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    Optional<User> foundUser = data.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();

    return foundUser;
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void deleteById(UUID userId) {
    data.remove(userId);
    // User 삭제 후 파일에 덮어쓰기
    FileSaveManager.saveToFile(getFile(), data);
  }
}
