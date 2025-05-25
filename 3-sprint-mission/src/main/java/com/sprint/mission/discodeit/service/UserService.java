package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;


public interface UserService {

    User create(UserCreateRequest userCreateDTO, Optional<BinaryContentCreateRequest> profileCreateDTO);
    UserDTO find(UUID id);
    UserDTO findByUsername(String username);
    List<UserDTO> findByName(String name);
    UserDTO findByEmail(String email);
    List<UserDTO> findAll();
    User update(UUID id, UserUpdateRequest userUpdateDTO, Optional<BinaryContentCreateRequest> profileCreateDTO);
    void delete(UUID id);
}