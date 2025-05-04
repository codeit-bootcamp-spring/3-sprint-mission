package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.service.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.service.dto.User.UserResponse;
import com.sprint.mission.discodeit.service.dto.User.UserUpdateRequest;
import java.util.List;
import java.util.UUID;


public interface UserService {
    public User create(UserCreateRequest userCreateRequest);

    public UserResponse find(UserFindRequest userFindRequest);

    public List<UserResponse> findAll();

    public User update(UserUpdateRequest request);

    public void delete(UUID userId);
}
