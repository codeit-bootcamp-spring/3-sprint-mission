package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User authenticate(String name, String password) {
        return null;
    }
}
