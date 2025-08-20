package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionContext {

    private final SessionRegistry sessionRegistry;


    public boolean getStatusFromSession(UUID userId) {
        Instant lastActiveAt = null;
        //오분전에까지 로그인했으면 로그인한 유저로 인정
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        List<Object> principals = sessionRegistry.getAllPrincipals();

        for (Object principal : principals) {
            if (principal instanceof DiscodeitUserDetails userDetails) {
                if (userDetails.getUserDto().getId().equals(userId)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails,
                        false);

                    for (SessionInformation session : sessions) {

                        // lastActiveAt이 현재 세션 값보다 클경우에만 계산
                        if (lastActiveAt == null || lastActiveAt.isAfter(
                            session.getLastRequest().toInstant())) {
                            lastActiveAt = session.getLastRequest().toInstant();
                        }

                    }
                }
            }
        }
        log.info("[유저 접속 상태 확인] id= {}, lastActiveAt= {}, 접속상태= {}", userId, lastActiveAt,
            lastActiveAt != null && !lastActiveAt.isBefore(instantFiveMinutesAgo));

        return lastActiveAt != null && !lastActiveAt.isBefore(instantFiveMinutesAgo);

    }

    public void expireUpdatedUserSession(UUID userId) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof DiscodeitUserDetails userDetails) {
                if (userDetails.getUserDto().getId() == userId) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails,
                        false);
                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }
                }
            }
        }
    }

    public List<String> getSessionIds(UUID userId) {
        List<String> sessionIds = new ArrayList<>();
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof DiscodeitUserDetails userDetails) {
                if (userDetails.getUserDto().getId() == userId) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails,
                        false);
                    for (SessionInformation session : sessions) {
                        sessionIds.add(session.getSessionId());
                    }
                }
            }
        }

        return sessionIds;
    }

}
