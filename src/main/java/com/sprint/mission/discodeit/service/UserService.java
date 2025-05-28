package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

  User create(UserRequestDTO userRequestDTO, BinaryContentDTO binaryContentDTO);

  UserResponseDTO findById(UUID id);

  List<UserResponseDTO> findAll();

  UserResponseDTO update(UUID id, UserUpdateDTO userUpdateDTO, BinaryContentDTO binaryContentDTO);

  void deleteById(UUID id);
}
