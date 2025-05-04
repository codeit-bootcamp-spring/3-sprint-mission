package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User create(CreateUserRequest request, Optional<CreateBinaryContentRequest> binaryContentRequest);
    boolean hasDuplicate(String username, String email);
    UserDTO find(UUID id);
    UserDTO findAll();
    User update(UUID userId, UpdateUserRequest request, Optional<CreateBinaryContentRequest> binaryContentRequest);
    void delete(UUID userId);
}
