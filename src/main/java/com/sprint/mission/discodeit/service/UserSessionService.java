package com.sprint.mission.discodeit.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserSessionService {

    private final Map<UUID, Instant> activeSessions = new ConcurrentHashMap<>();
    private final Duration sessionTimeout = Duration.ofMinutes(30); // JWT 토큰 만료 시간과 동일

    public void markUserOnline(UUID userId) {
        activeSessions.put(userId, Instant.now());
        log.debug("User {} marked as online", userId);
    }

    public void markUserOffline(UUID userId) {
        activeSessions.remove(userId);
        log.debug("User {} marked as offline", userId);
    }

    public boolean isUserOnline(UUID userId) {
        Instant lastActivity = activeSessions.get(userId);
        if (lastActivity == null) {
            return false;
        }
        return Instant.now().isBefore(lastActivity.plus(sessionTimeout));
    }

    public Set<UUID> getOnlineUsers() {
        Instant now = Instant.now();
        return activeSessions.entrySet().stream()
            .filter(entry -> now.isBefore(entry.getValue().plus(sessionTimeout)))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    // 만료된 세션 정리 (1분마다 실행)
    @Scheduled(fixedRate = 60000)
    public void cleanExpiredSessions() {
        Instant now = Instant.now();
        int beforeSize = activeSessions.size();
        activeSessions.entrySet().removeIf(entry ->
            now.isAfter(entry.getValue().plus(sessionTimeout)));
        int afterSize = activeSessions.size();

        if (beforeSize != afterSize) {
            log.debug("Cleaned {} expired sessions. Active sessions: {}",
                beforeSize - afterSize, afterSize);
        }
    }
}

