package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public interface AuthService {

    UserDto updateRole(UUID uuid, Role role);

    JwtDto refreshToken(String refreshToken, HttpServletResponse response);
}
