package com.sprint.mission.discodeit.auth.jwt.registry;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final JwtTokenProvider jwtTokenProvider;

    // 사용자별 활성 JWT 큐
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    // 동시 로그인 허용 개수
    private final int maxActiveJwtCount;

    public InMemoryJwtRegistry(
        JwtTokenProvider jwtTokenProvider,
        @Value("${jwt.max-concurrent-sessions:1}") int maxActiveJwtCount
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.maxActiveJwtCount = maxActiveJwtCount;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            // If the queue exceeds the max size, remove the oldest token
            if (queue.size() >= maxActiveJwtCount) {
                JwtInformation deprecatedJwtInformation = queue.poll();// Remove the oldest token
                if (deprecatedJwtInformation != null) {
                    removeTokenIndex(
                        deprecatedJwtInformation.getAccessToken(),
                        deprecatedJwtInformation.getRefreshToken()
                    );
                }
            }
            queue.add(jwtInformation); // Add the new token
            addTokenIndex(
                jwtInformation.getAccessToken(),
                jwtInformation.getRefreshToken()
            );
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.computeIfPresent(userId, (key, queue) -> {
            queue.forEach(jwtInformation -> {
                removeTokenIndex(
                    jwtInformation.getAccessToken(),
                    jwtInformation.getRefreshToken()
                );
            });
            queue.clear(); // Clear the queue for this user
            return null; // Remove the user from the registry
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> q = origin.get(userId);
        if (q == null) return false;
        // RT가 유효한 엔트리가 하나라도 있으면 로그인 중임
        return q.stream().anyMatch(info -> jwtTokenProvider.validateRefreshToken(info.getRefreshToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshTokenIndexes.contains(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.computeIfPresent(newJwtInformation.getUserDto().id(), (key, queue) -> {
            queue.stream().filter(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(jwtInformation -> {
                    removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
                    jwtInformation.rotate(
                        newJwtInformation.getAccessToken(),
                        newJwtInformation.getRefreshToken()
                    );
                    addTokenIndex(
                        newJwtInformation.getAccessToken(),
                        newJwtInformation.getRefreshToken()
                    );
                });
            return queue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(jwtInformation -> {
                // RT가 만료됐을 때만
                boolean isExpired = !jwtTokenProvider.validateRefreshToken(jwtInformation.getRefreshToken());
                if (isExpired) {
                    removeTokenIndex(
                        jwtInformation.getAccessToken(),
                        jwtInformation.getRefreshToken()
                    );
                }
                return isExpired;
            });
            return queue.isEmpty();
        });
    }

    @Override
    public void invalidateByRefreshToken(String refreshToken) {
        if (refreshToken == null) return;
        for (Map.Entry<UUID, Queue<JwtInformation>> e : origin.entrySet()) {
            Queue<JwtInformation> q = e.getValue();
            if (q == null) continue;
            boolean removed = q.removeIf(info -> {
                boolean match = refreshToken.equals(info.getRefreshToken());
                if (match) removeTokenIndex(info.getAccessToken(), info.getRefreshToken()); // ← 추가
                return match;
            });
            if (removed && q.isEmpty()) origin.remove(e.getKey());
            if (removed) return;
        }
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.add(accessToken);
        refreshTokenIndexes.add(refreshToken);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        accessTokenIndexes.remove(accessToken);
        refreshTokenIndexes.remove(refreshToken);
    }
}
