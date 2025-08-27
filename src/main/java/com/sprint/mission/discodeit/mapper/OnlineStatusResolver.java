package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineStatusResolver {
    private final JwtRegistry jwtRegistry;

    public boolean resolve(UUID userId) {
        return jwtRegistry.hasActiveJwtInformationByUserId(userId);
    }
}