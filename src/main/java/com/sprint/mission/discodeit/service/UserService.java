package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest);

    UserResponse find(UUID userId);

    List<UserResponse> findAll();


    UserResponse update(UUID userId, UserUpdateRequest request,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);


    UserResponse delete(UUID userId);

}
