package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(CreateUserRequest request, Optional<AddBinaryContentRequest> addBinaryContentRequest);
    UserDTO find(UUID userId);
    List<UserDTO> findAll();
    User update(UUID userId, UpdateUserRequest updateUserRequest, Optional<AddBinaryContentRequest> addBinaryContentRequest);
    void delete(UUID userId);
}
