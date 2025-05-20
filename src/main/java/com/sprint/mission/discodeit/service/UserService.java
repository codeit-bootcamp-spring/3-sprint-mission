package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  public User create(UserCreateRequest userRequest,
      Optional<BinaryContentCreateRequest> binaryContentRequest);

  public List<UserDto> findAll();

  public UserDto find(UUID id);

  public void update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> binaryContentRequest);

  public void delete(UUID userId);

}
