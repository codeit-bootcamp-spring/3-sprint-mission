package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.store.JwtInformation;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserDto updateRole(RoleUpdateRequest request);

    JwtInformation reIssueAccessByRefreshToken(HttpServletResponse response, String refreshToken);
}
