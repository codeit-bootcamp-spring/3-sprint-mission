package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

  UserResponseDto create(UserRequestDto userRequestDTO, BinaryContentDto binaryContentDto);

  UserResponseDto findById(UUID id);

  List<UserResponseDto> findAll();

  UserResponseDto update(UUID id, UserUpdateDto userUpdateDTO, BinaryContentDto binaryContentDto);

  void deleteById(UUID id);
}
