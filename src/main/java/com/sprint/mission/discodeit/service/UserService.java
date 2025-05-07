package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.DTO.Request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Request.UserCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.DTO.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
   User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> portraitCreateRequest);
   UserDTO find(UUID userId);
   List<UserDTO> findAll();
   User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> portraitCreateRequest);
   void delete(UUID userId);
}
