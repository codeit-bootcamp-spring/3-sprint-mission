package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

  UserResponse login(LoginRequest request);
}