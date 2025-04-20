package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  // 유저 생성
  void save(User user);

  // 유저 단건 조회 (ID로)
  Optional<User> getUserById(UUID id);

  // 모든 유저 조회
  List<User> getAllUsers();

  // 유저 수정
  void update(User user);

  // 유저 삭제
  void delete(UUID id);

}
