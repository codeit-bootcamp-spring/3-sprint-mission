package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

  public UserDto login(LoginRequest loginRequest);
}
