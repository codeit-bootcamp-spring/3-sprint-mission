package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.User.LoginRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;

public interface AuthService {

    UserDto login(LoginRequest loginRequest);
}
