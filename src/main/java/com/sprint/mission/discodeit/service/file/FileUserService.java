package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService {

  private final String filePath = "users.dat";
  private final Map<UUID, User> data = loadFromFile();

  @Override
  public UserResponse create(UserCreateRequest request) {
    throw new UnsupportedOperationException("FileUserService에서는 이 기능을 지원하지 않습니다.");
  }

  @Override
  public User create(String username) {
    User user = new User(username);
    data.put(user.getId(), user);
    saveToFile();
    return user;
  }

  @Override
  public UserResponse findById(UUID id) {
    throw new UnsupportedOperationException("FileUserService에서는 이 기능을 지원하지 않습니다.");
  }

  @Override
  public List<UserResponse> findAll() {
    throw new UnsupportedOperationException("FileUserService에서는 이 기능을 지원하지 않습니다.");
  }

  @Override
  public User update(UUID id, String newUsername) {
    User user = data.get(id);
    if (user != null) {
      user.updateUsername(newUsername);
      saveToFile();
    }
    return user;
  }

  @Override
  public UserResponse update(UserUpdateRequest request) {
    throw new UnsupportedOperationException("FileUserService에서는 이 기능을 지원하지 않습니다.");
  }

  @Override
  public User delete(UUID id) {
    User user = data.get(id);
    if (user != null) {
      data.remove(id);
      saveToFile();
    }
    return user;
  }

  private void saveToFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<UUID, User> loadFromFile() {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
      return (Map<UUID, User>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HashMap<>();
    }
  }
}