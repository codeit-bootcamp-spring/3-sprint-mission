package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;

public interface AuthService {
    UserResponseDTO login(LoginDTO loginDTO);
}
