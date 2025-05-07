package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.DTO.Request.LoginRequest;

public interface AuthService {
    User Login(LoginRequest loginRequest);
}
