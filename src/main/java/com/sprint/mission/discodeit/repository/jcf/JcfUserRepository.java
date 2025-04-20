package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JcfUserRepository implements UserRepository {
  private final Map<UUID, User> userMap;

  public JcfUserRepository() {
    this.userMap = new HashMap<>();
  }

  @Override
  public void save(User user) {
    userMap.put(user.getId(), user);
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.ofNullable(userMap.get(id));
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(userMap.values());
  }

  @Override
  public void update(User user) {
    if (!userMap.containsKey(user.getId())) {
      throw new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다: " + user.getId());
    }
    userMap.put(user.getId(), user);
  }

  @Override
  public void delete(UUID id) {
    if (!userMap.containsKey(id)) {
      throw new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다: " + id);
    }
    userMap.remove(id);
  }

  /*
 JcfUserRepository는 Map<UUID, User>를 사용하여 데이터를 관리하고, CRUD 기능을 수행함.
JcfUserService는 UserRepository를 주입받아 유저 데이터를 관리하고, 유저 생성, 수정, 삭제 등 비즈니스 로직을 담당함.

createUser(), updateUserName(), updateUserEmail(), deleteUser() 등 서비스 로직에서 데이터를 수정한 후,
JcfUserRepository의 update(), save(), delete() 메서드를 호출해 데이터 저장소에 반영함.

JcfUserService는 비즈니스 로직만 처리하고, 데이터 저장 및 조회는 JcfUserRepository가 담당한다.
   */
}