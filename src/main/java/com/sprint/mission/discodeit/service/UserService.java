package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.user.FriendReqeustDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

  User create(UserRequestDTO userRequestDTO, BinaryContentDTO binaryContentDTO);

  UserResponseDTO findById(UUID id);

  UserResponseDTO findByEmail(String email);

  List<UserResponseDTO> findByNameContaining(String name);

  List<UserResponseDTO> findAll();

  UserResponseDTO update(UUID id, UserRequestDTO userRequestDTO, BinaryContentDTO binaryContentDTO);

  void deleteById(UUID id);

  void addFriend(FriendReqeustDTO friendReqeustDTO);

  void deleteFriend(FriendReqeustDTO friendReqeustDTO);
}
