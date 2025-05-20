package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface AuthService {
    UserStatus login(UUID userId, String password);
    void validatePassword(User user, String password);
    UserStatus updateUserStatus(UUID userId);
}
