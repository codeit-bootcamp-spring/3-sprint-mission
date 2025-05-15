package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginRequest;

public interface AuthService {
    public AuthLoginReponse login(AuthLoginRequest request);
}
