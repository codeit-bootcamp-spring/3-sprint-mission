package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(CreateUserRequest request, Optional<AddBinaryContentRequest> addBinaryContentRequest);
    User find(UUID userId);
    List<User> findAll();
    User updatePassword(UpdatePasswordRequest updatePasswordRequest);
    User updateProfile(UpdateProfileRequest updateProfileRequest);
    void delete(UUID userId);
}
