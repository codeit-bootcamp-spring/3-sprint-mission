package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
  private static final String FILE_PATH = "users.ser";
  private final Map<UUID, User> userData;
  private ChannelService channelService;

  public FileUserService() {
    this.userData = loadUserData(); // 파일에서 데이터 로드
  }

  public void setChannelService(ChannelService channelService) {
    this.channelService = channelService;
  }

  private void saveUserData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(userData); // 데이터를 파일에 저장
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<UUID, User> loadUserData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>(); // 파일이 없으면 빈 Map 반환
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject(); // 파일에서 데이터 읽기
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>(); // 오류가 발생하면 빈 Map 반환
    }
  }

  @Override
  public User createUser(String username, String email) {
    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }
    User user = new User(username, email);
    userData.put(user.getId(), user);
    saveUserData();
    return user;
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.ofNullable(userData.get(id));
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(userData.values());
  }

  public void updateUserName(UUID id, String name) {
    User user = getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));
    user.updateName(name);
    saveUserData();
  }

  public void updateUserEmail(UUID id, String email) {
    User user = getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    if (user.getEmail().equalsIgnoreCase(email)) {
      throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
    }

    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }

    user.updateEmail(email);
    saveUserData();
  }

  @Override
  public void deleteUser(UUID id) {
    User user = getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    if (channelService != null) {
      channelService.deleteChannelsCreatedByUser(id);
      channelService.removeUserFromAllChannels(id);
    }

    userData.remove(id);
    saveUserData();
  }

  private boolean isEmailDuplicate(String email) {
    return userData.values().stream()
        .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)); // 이메일 중복 검사
  }
}
