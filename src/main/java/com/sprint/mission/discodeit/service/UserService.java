package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    UserDTO get(UUID id);
    List<UserDTO> getAll();
    User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    void delete(UUID id);
}
