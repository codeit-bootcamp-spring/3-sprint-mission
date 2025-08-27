package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest);

    com.sprint.mission.discodeit.dto.data.UserDto find(UUID userId);

    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest);

    void delete(UUID userId);

    boolean isUserOwner(UUID targetUserId, UUID currentUserId);
}
