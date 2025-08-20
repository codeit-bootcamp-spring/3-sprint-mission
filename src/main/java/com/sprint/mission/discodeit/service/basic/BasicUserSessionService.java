package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.UserSessionService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserSessionService implements UserSessionService {

    private static final long ONLINE_THRESHOLD_MILLIS = Duration.ofMinutes(5).toMillis();

    private final SessionRegistry sessionRegistry;

    @Override
    public boolean isUserOnline(String username) {
        return  sessionRegistry.getAllPrincipals().stream()
            .filter(p -> p instanceof UserDetails)
            .map(UserDetails.class::cast)
            .filter(u -> u.getUsername().equals(username))
            .flatMap(u -> sessionRegistry.getAllSessions(u, false).stream())
            .anyMatch(session -> !session.isExpired() &&
                session.getLastRequest().getTime() > System.currentTimeMillis() - ONLINE_THRESHOLD_MILLIS);
    }
}
