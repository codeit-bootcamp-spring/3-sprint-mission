package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Named;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
public class OnlineStatusChecker {

    private final SessionRegistry sessionRegistry;

    public OnlineStatusChecker(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Named("isOnline")
    public boolean isOnline(User user) {
        return sessionRegistry.getAllPrincipals().stream()
            .filter(p -> p instanceof DiscodeitUserDetails dud && dud.getUserDto().id().equals(user.getId()))
            .map(p -> sessionRegistry.getAllSessions(p, false))
            .anyMatch(sessions -> sessions.stream().anyMatch(si -> !si.isExpired()));
    }
}
