package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest request,
                   Optional<BinaryContentCreateRequest> profileRequest) throws IOException;

    UserDto find(UserFindRequest request);

    List<UserDto> findAll();

    UserDto update(UUID userId,
                   UserUpdateRequest request,
                   Optional<BinaryContentCreateRequest> profileRequest);

    void delete(UUID userId);
}