package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    public User login(LoginRequest loginRequest);
}
