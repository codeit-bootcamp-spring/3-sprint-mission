package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.LoginRequest;

public interface AuthService {
    public AuthLoginReponse login(LoginRequest request);
}
