package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionStatusService {

    private final SessionRegistry sessionRegistry;

    public boolean isUserLoggedIn(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .anyMatch(userDetails -> userDetails.getUserDto().id().equals(userId));
    }
}
