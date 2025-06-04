package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest userCreateRequest,
            BinaryContentCreateRequest profileCreateRequest);

    List<UserDto> findAll();

    UserDto find(UUID userId);

    UserDto update(UUID userId, UserUpdateRequest updateRequest,
            BinaryContentCreateRequest profileCreateRequest);

    void delete(UUID userId);
}
