package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(CreateUserRequest request);
    User create(CreateUserRequest createUserRequest, CreateBinaryContentRequest binaryContentRequest);

    User find(UUID userId);
    List<User> findAll();
    User update(UUID userId, UpdateUserRequest updateUserRequest);
    User update(UUID userId, UpdateUserRequest request,
                CreateBinaryContentRequest profileCreateRequest);
    void delete(UUID userId);
}
