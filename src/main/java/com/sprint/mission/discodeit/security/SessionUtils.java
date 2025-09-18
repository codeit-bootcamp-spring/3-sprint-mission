package com.sprint.mission.discodeit.security;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionUtils {

    private final SessionRegistry sessionRegistry;

    public boolean isUserLoggedIn(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
            .filter(principal -> principal instanceof DiscodeitUserDetails)
            .map(DiscodeitUserDetails.class::cast)
            .filter(details -> details.getUserDto().id().equals(userId))
            .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
            .anyMatch(session -> !session.isExpired());
    }

}