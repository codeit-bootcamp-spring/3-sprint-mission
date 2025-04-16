package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JcfUserServiceUsingMap implements UserService {

  private final Map<UUID,User> data;

  public JcfUserServiceUsingMap() {
    this.data = new HashMap<>();
  }

  @Override
  public User createUser(String username, String email) {
    User user = new User(username, email);
    data.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.ofNullable(data.get(id));
    //return Optional.ofNullable(users.get(id)).orElseThrow(() -> new IllegalArgumentException("조회할 User를 찾지 못했습니다."));
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void updateUserName(UUID id, String name) {
    User user = getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id)); // 존재하는지 확인
    //User user = Optional.ofNullable(users.get(id))
    //        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));
    user.updateName(name);
  }

  @Override
  public void updateUserEmail(UUID id, String email) {
    User user = getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id)); // 존재하는지 확인
    user.updateEmail(email);
  }

  @Override
  public void deleteUser(UUID id) {
    if (data.remove(id) == null) {
      throw new IllegalArgumentException("삭제 실패: 해당 ID (" + id + ")의 유저를 찾을 수 없습니다.");
    }
  }

  // 시간복잡도
  // Create - data.put() -> O(1)
  // Read - data.get() -> O(1)  & data.values() -> O(N) + new ArrayList<>() -> O(N)
  // Update - data.get() -> O(1)
  // Delete - date.remove() -> O(1)
}