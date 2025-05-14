package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
  // 1. 유저 생성
  User createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);

  // 2. 유저 단건 조회
  //Optional<User> getUserById(UUID id);
  Optional<UserDto> find(UUID userId);

  // 3. 유저 전체 조회
  //List<User> getAllUsers();
  List<UserDto> findAll();

  // 4. 유저 수정
  //void updateUserName(UUID id,String name);
  //void updateUserEmail(UUID id,String email);
  User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);

  // 5. 유저 삭제
  void delete(UUID userId);
}
