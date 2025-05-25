package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    User login(AuthRequest loginRequest);
}
