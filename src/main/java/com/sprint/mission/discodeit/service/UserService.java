package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserResponse;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    public User create(UserCreateRequest userCreateRequest,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);

    public UserResponse find(UserFindRequest userFindRequest);

    public List<UserResponse> findAll();

    public User update(UserUpdateRequest request,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);

    public void delete(UUID userId);
}
