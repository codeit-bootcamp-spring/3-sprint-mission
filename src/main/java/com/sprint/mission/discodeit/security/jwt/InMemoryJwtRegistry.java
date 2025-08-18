package com.sprint.mission.discodeit.security.jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        origin.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);
        queue.add(jwtInformation);

        while (queue.size() > maxActiveJwtCount) {
            queue.poll(); // 가장 오래된 로그인 제거
        }
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
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
                    break;
                }
            }
        });
    }
}