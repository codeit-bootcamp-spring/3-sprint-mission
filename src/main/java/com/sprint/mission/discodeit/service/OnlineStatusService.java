package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OnlineStatusService {
    private final SessionRegistry sessionRegistry;

    public boolean isUserOnline(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                .map(DiscodeitUserDetails.class::cast)
                .anyMatch(details -> details.getUserDto().id().equals(userId));
    }
}
