package com.sprint.mission.discodeit.security.jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSeen = new ConcurrentHashMap<>();

    private final int maxActiveJwtCount = 1;

    private final long onlineIdleWindowMs;

    public InMemoryJwtRegistry(
            @Value("${jwt.online-idle-window-ms:120000}") long onlineIdleWindowMs
    ) {
        this.onlineIdleWindowMs = onlineIdleWindowMs;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        origin.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);
        queue.add(jwtInformation);

        while (queue.size() > maxActiveJwtCount) {
            queue.poll(); // 가장 오래된 로그인 제거
        }

        lastSeen.put(userId, System.currentTimeMillis());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
        lastSeen.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId) && !origin.get(userId).isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.values().forEach(queue -> {
            for (JwtInformation info : queue) {
                if (info.getRefreshToken().equals(refreshToken)) {
                    info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                    lastSeen.put(newJwtInformation.getUserDto().id(), System.currentTimeMillis());
                    break;
                }
            }
        });
    }

    @Override
    public void markAlive(UUID userId) {
        lastSeen.put(userId, System.currentTimeMillis());
    }

    @Override
    public boolean isOnline(UUID userId) {
        if (!hasActiveJwtInformationByUserId(userId)) {
            return false;
        }
        Long seen = lastSeen.get(userId);
        if (seen == null) {
            return false;
        }
        return (System.currentTimeMillis() - seen) <= onlineIdleWindowMs;
    }

    @Override
    public UUID invalidateJwtInformationByRefreshToken(String refreshToken) {
        UUID targetUserId = null;

        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> queue = entry.getValue();

            boolean removed = queue.removeIf(info -> info.getRefreshToken().equals(refreshToken));
            if (removed) {
                targetUserId = userId;

                if (queue.isEmpty()) {
                    origin.remove(userId);
                    lastSeen.remove(userId);
                }
                break;
            }
        }
        return targetUserId;
    }
}