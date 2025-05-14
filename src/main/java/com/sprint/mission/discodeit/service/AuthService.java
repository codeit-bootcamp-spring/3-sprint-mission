package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

public interface AuthService {
    public UserResponse login(LoginRequest loginRequest) throws Exception;
}
