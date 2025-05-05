package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserResponse create(UserCreateRequest request);

  User create(String username);

  UserResponse findById(UUID id);


  List<UserResponse> findAll();

  User update(UUID id, String newUsername);

  UserResponse update(UserUpdateRequest request);

  // UserService.java
  UserResponse delete(UUID id);

}
