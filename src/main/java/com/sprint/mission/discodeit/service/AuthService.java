package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;

public interface AuthService {

  JwtInformation refreshToken(String refreshToken);
}
