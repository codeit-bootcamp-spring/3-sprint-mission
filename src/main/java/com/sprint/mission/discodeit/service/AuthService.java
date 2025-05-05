package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.entity.User;

public interface AuthService {
    User authenticate(String name, String password);
}
