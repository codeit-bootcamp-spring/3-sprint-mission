package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.auth.LoginRequestDto;

import java.util.Optional;

public interface AuthService {

    public Optional<User> login(LoginRequestDto loginRequestDto);
}
