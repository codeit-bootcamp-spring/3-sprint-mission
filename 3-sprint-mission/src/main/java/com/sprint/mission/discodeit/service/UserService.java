package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {

  UserDto create(UserCreateRequest userCreateDto,
      Optional<BinaryContentCreateRequest> profileCreateDto);

  UserDto find(UUID id);

  UserDto findByUsername(String username);

  UserDto findByEmail(String email);

  List<UserDto> findAll();

  UserDto update(UUID id, UserUpdateRequest userUpdateDto,
      Optional<BinaryContentCreateRequest> profileCreateDto);

  void delete(UUID id);
}