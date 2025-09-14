package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserDto updateUserRole(UserRoleUpdateRequest request);

    JwtDto refreshToken(String refreshToken, HttpServletResponse response);

}
