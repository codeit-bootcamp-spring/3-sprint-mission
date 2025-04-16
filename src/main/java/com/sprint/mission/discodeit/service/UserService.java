package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
  // 1. 유저 생성
  User createUser(String username, String email);
  // 2. 유저 단건 조회
  User getUserById(UUID id);
  // 3. 유저 전체 조회
  List<User> getAllUsers();
  // 4. 유저 수정
  void updateUserName(UUID id,String name);
  void updateUserEmail(UUID id,String email);
  // 5. 유저 삭제
  void deleteUser(UUID id);
}
