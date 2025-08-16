package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    JwtDto refresh(String refreshToken, HttpServletResponse response);
}
