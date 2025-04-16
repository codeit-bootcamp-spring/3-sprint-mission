package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.stream.Collectors;

public class FileUserService implements UserService {

  private final String FILE_PATH;

  private FileUserService(String filePath) {
    FILE_PATH = filePath;
  }

  public static FileUserService from(String filePath) {
    return new FileUserService(filePath);
  }

  public static FileUserService createDefault() {
    return new FileUserService("data/users.ser");
  }

  @Override
  public User createUser(String email, String name, String password) {
    Map<UUID, User> usersRepository = loadData();
    validateUserEmail(usersRepository, email);
    User user = User.create(email, name, password);
    usersRepository.put(user.getId(), user);
    saveData(usersRepository);
    return user;
  }

  private void validateUserEmail(Map<UUID, User> usersRepository, String email) {
    if (usersRepository.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    Map<UUID, User> usersRepository = loadData();
    return Optional.ofNullable(usersRepository.get(id));
  }

  @Override
  public List<User> searchUsersByName(String name) {
    Map<UUID, User> usersRepository = loadData();
    return usersRepository.values().stream()
        .filter(user -> user.getName().contains(name))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    Map<UUID, User> usersRepository = loadData();
    return usersRepository.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public List<User> getAllUsers() {
    Map<UUID, User> usersRepository = loadData();
    return new ArrayList<>(usersRepository.values());
  }

  @Override
  public Optional<User> updateUser(UUID id, String name, String password) {
    Map<UUID, User> usersRepository = loadData();
    Optional<User> userOpt = Optional.ofNullable(usersRepository.get(id))
        .map(user -> {
          if (name != null) {
            user.updateName(name);
          }
          if (password != null) {
            user.updatePassword(password);
          }
          return user;
        });

    if (userOpt.isPresent()) {
      saveData(usersRepository);
    }
    return userOpt;
  }

  @Override
  public Optional<User> deleteUser(UUID id) {
    Map<UUID, User> usersRepository = loadData();
    Optional<User> user = Optional.ofNullable(usersRepository.remove(id));
    if (user.isPresent()) {
      saveData(usersRepository);
    }
    return user;
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, User> loadData() {
    Map<UUID, User> usersRepository = new HashMap<>();
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      createData();
      return usersRepository;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Object obj = ois.readObject();
      if (obj instanceof Map) {
        usersRepository.putAll((Map<UUID, User>) obj);
      }
    } catch (FileNotFoundException e) {
      // 파일이 없을 경우 초기 상태 유지
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return usersRepository;
  }

  private void createData() {
    File file = new File(FILE_PATH);
    File parentDir = file.getParentFile();

    if (parentDir != null && !parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        System.err.println("폴더 생성 실패: " + parentDir.getAbsolutePath());
        return;
      }
    }

    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveData(Map<UUID, User> usersRepository) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(usersRepository);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}