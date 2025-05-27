package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserStatusRepository implements UserStatusRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory
  ) {
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
        UserStatus.class.getSimpleName());
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    Path path = resolvePath(userStatus.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(userStatus);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return userStatus;
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    UserStatus userStatusNullable = null;
    Path path = resolvePath(id);
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        userStatusNullable = (UserStatus) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Optional.ofNullable(userStatusNullable);
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return findAll().stream()
        .filter(userStatus -> userStatus.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    try (Stream<Path> paths = Files.list(DIRECTORY)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              Object obj = ois.readObject();
              if (obj instanceof UserStatus status) {
                return status;
              } else {
                System.err.println("❌ 예상과 다른 객체: " + path.getFileName());
                return null;
              }
            } catch (EOFException e) {
              System.err.println("⚠️ EOFException (파일 끝 도달): " + path.getFileName());
              return null;
            } catch (IOException | ClassNotFoundException e) {
              System.err.println("⚠️ 역직렬화 실패: " + path.getFileName());
              return null;
            }
          })
          .filter(status -> status != null)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException("📂 디렉토리 접근 실패", e);
    }
  }


  @Override
  public boolean existsById(UUID id) {
    Path path = resolvePath(id);
    return Files.exists(path);
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteByUserId(UUID userId) {
    this.findByUserId(userId)
        .ifPresent(userStatus -> this.deleteById(userStatus.getId()));
  }
}
