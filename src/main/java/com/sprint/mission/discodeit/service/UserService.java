package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  UserResponse create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);

  UserDto find(UUID userId);

  List<UserDto> findAll();


  UserResponse update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);


  UserResponse delete(UUID userId);

}
