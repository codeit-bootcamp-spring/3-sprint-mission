package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface AuthService {
    User login(String username, String password);
    UserStatus updateUserStatus(UUID userId);
}