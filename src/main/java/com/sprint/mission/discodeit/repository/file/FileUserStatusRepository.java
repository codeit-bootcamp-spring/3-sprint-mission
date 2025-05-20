package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";

  public FileUserStatusRepository() {

    //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
    this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data",
        UserStatus.class.getSimpleName());
    //  지정한 path에 디렉토리 없으면 생성
    if (!Files.exists(this.DIRECTORY)) {
      try {
        Files.createDirectories(this.DIRECTORY);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

  // TODO : 나중에 Util로 빼기
  private Path resolvePath(UUID id) {
    // 객체를 저장할 파일 path 생성
    return this.DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    Path filePath = this.resolvePath(userStatus.getId());
    try (
        // 파일과 연결되는 스트림 생성
        FileOutputStream fos = new FileOutputStream(filePath.toFile());
        // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
        ObjectOutputStream oos = new ObjectOutputStream(fos);
    ) {

      oos.writeObject(userStatus);
      return userStatus;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserStatus> findById(UUID userStatusId) {
    // 객체가 저장된 파일 path
    Path filePath = this.resolvePath(userStatusId);

    try (
        // 파일과 연결되는 스트림 생성
        FileInputStream fis = new FileInputStream(String.valueOf(filePath));
        // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
        ObjectInputStream ois = new ObjectInputStream(fis);
    ) {
      UserStatus userStatusNullable = (UserStatus) ois.readObject();
      return Optional.ofNullable(userStatusNullable);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return this.findAll().stream()
        .filter(userStatus -> userStatus.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    try (Stream<Path> paths = Files.list(this.DIRECTORY)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try ( // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(path));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
              return (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean existsById(UUID userStatusId) {
    Path path = resolvePath(userStatusId);
    return Files.exists(path);
  }

  @Override
  public void deleteById(UUID userStatusId) {
    // 객체가 저장된 파일 path
    Path filePath = this.resolvePath(userStatusId);
    try {
      if (Files.exists(filePath)) {
        Files.delete(filePath);
      } else {
        throw new FileNotFoundException("File does not exist");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteByUserId(UUID userId) {
    this.findByUserId(userId).ifPresent(userStatus -> this.deleteById(userStatus.getId()));
  }

}
