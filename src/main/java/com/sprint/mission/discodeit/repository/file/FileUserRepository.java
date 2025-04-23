package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
  private static final String FILE_PATH = "users.ser";
  private final Map<UUID, User> userData;

  public FileUserRepository() {
    this.userData = loadUserData();
  }

  // 파일로부터 유저 데이터 로드
  private Map<UUID, User> loadUserData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>();
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  // 유저 데이터를 파일로 저장
  private void saveUserData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(userData);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 유저 저장 (create)
  @Override
  public void save(User user) {
    userData.put(user.getId(), user);
    saveUserData();
  }

  // 유저 단건 조회
  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.ofNullable(userData.get(id));
  }

  // 모든 유저 조회
  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(userData.values());
  }

  // 유저 수정
  @Override
  public void update(User user) {
    if (!userData.containsKey(user.getId())) {
      throw new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다: " + user.getId());
    }
    userData.put(user.getId(), user); // 업데이트는 덮어쓰기
    saveUserData();
  }

  // 유저 삭제
  @Override
  public void delete(UUID id) {
    userData.remove(id);
    saveUserData();
  }
}
