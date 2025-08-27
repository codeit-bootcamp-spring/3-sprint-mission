package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserSessionService implements UserSessionService {

    private final JwtRegistry jwtRegistry;

    @Override
    public boolean isUserOnline(String username) {
        return jwtRegistry.hasActiveJwtInformationByUsername(username);
    }
}
