package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileUserRepository implements UserRepository {

  @Value("${discodeit.repository.fileDirectory}")
  private String FILE_Directory;
  private final String FILE_NAME = "user.ser";

  public Path getFilePath() {
    return Paths.get(FILE_Directory, FILE_NAME);
  }

  //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
  public List<User> readFiles() {
    try {
      if (!Files.exists(getFilePath()) || Files.size(getFilePath()) == 0) {
        return new ArrayList<>();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<User> users = new ArrayList<>();
    try (ObjectInputStream reader = new ObjectInputStream(
        new FileInputStream(getFilePath().toFile()))) {
      while (true) {
        try {
          users.add((User) reader.readObject());
        } catch (EOFException e) {
          break;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return users;
  }


  //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
  public void writeFiles(List<User> users) {
    try {
      Files.createDirectories(getFilePath().getParent());
      try (ObjectOutputStream writer = new ObjectOutputStream(
          new FileOutputStream(getFilePath().toFile()))) {
        for (User user : users) {
          writer.writeObject(user);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  public User save(User user) {
    List<User> users = new ArrayList<>(readFiles());
    users.add(user);
    writeFiles(users);
    return user;
  }

  @Override
  public List<User> read() {
    return readFiles();
  }

  @Override
  public Optional<User> readById(UUID id) {
    return readFiles().stream()
        .filter((user) -> user.getId().equals(id))
        .findAny();
  }

  @Override
  public Boolean duplicateCheck(User user) {
    List<User> users = readFiles();
    //user의 name이나 Email이 중복인지 검사
      if (users.stream().anyMatch((c) -> c.getUsername().equals(user.getUsername()))
          || users.stream()
          .anyMatch((c) -> c.getEmail().equals(user.getEmail())))
      //중복이면 true
      {
          return true;
      } else
      //중복이 아니면 false return
      {
          return false;
      }

  }

  @Override
  public void update(UUID id, User user) {
    List<User> users = readFiles();
    users.stream()
        .filter((c) -> c.getId().equals(id))
        .forEach((c) -> {
          c.setUsername(user.getUsername());
          c.setPassword(user.getPassword());
          c.setUpdatedAt(Instant.now());
          c.setEmail(user.getEmail());
          c.setFriends(user.getFriends());
          if (user.getProfileId() != null) {
            c.setProfileId(user.getProfileId());
          }
        });
    writeFiles(users);
  }

  @Override
  public void delete(UUID userId) {
    List<User> users = readFiles();
    users.removeIf(user -> user.getId().equals(userId));
    writeFiles(users);
  }
}
