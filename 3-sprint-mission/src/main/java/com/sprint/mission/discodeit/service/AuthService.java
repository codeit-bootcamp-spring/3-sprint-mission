package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

  UserDTO login(LoginRequest loginRequest);
}
