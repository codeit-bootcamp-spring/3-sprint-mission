package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {

  UserDTO create(UserCreateRequest userCreateDTO,
      Optional<BinaryContentCreateRequest> profileCreateDTO);

  UserDTO find(UUID id);

  UserDTO findByUsername(String username);

  UserDTO findByEmail(String email);

  List<UserDTO> findAll();

  UserDTO update(UUID id, UserUpdateRequest userUpdateDTO,
      Optional<BinaryContentCreateRequest> profileCreateDTO);

  void delete(UUID id);
}