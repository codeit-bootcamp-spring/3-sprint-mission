package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {
    User Login(LoginRequest loginRequest);
}
