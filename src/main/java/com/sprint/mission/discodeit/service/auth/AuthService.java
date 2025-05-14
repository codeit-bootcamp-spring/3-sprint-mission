package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;

public interface AuthService {

    public UserResponseDto login(LoginRequestDto loginRequestDto);
}
