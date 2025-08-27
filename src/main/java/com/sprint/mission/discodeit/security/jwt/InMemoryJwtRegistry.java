package com.sprint.mission.discodeit.security.jwt;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry{

    // <userId, Queue<JwtInformation>>
    private final ConcurrentHashMap<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount;

    public InMemoryJwtRegistry(@Value("${jwt.max-active-session:1}") int maxActiveJwtCount) {
        this.maxActiveJwtCount = maxActiveJwtCount;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        origin.compute(userId, (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }

            // 최대 동시 로그인 수 제어
            while (queue.size() >= maxActiveJwtCount) {
                JwtInformation removed = queue.poll();
                if (removed != null) {
                    log.info("최대 동시 로그인 수 초과로 기존 세션 무효화: userId={}", userId);
                }
            }

            queue.offer(jwtInformation);
            log.info("JWT 정보 등록 완료: userId={}", userId);
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> removed = origin.remove(userId);
        if (removed != null && !removed.isEmpty()) {
            log.info("사용자 JWT 정보 무효화 완료: userId={}, 무효화된 세션 수={}", userId, removed.size());
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> jwtQueue = origin.get(userId);
        return jwtQueue != null && !jwtQueue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(jwt -> accessToken.equals(jwt.getAccessToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(jwt -> refreshToken.equals(jwt.getRefreshToken()));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID userId = newJwtInformation.getUserDto().id();

        origin.computeIfPresent(userId, (key, queue) -> {
            // 기존 리프레시 토큰을 가진 JWT 정보 찾아서 교체
            Queue<JwtInformation> newQueue = new ConcurrentLinkedQueue<>();
            boolean found = false;

            for (JwtInformation jwt : queue) {
                if (refreshToken.equals(jwt.getRefreshToken())) {
                    newQueue.offer(newJwtInformation);
                    found = true;
                    log.info("JWT 토큰 로테이션 완료: userId={}", userId);
                } else {
                    newQueue.offer(jwt);
                }
            }

            if (!found) {
                log.warn("로테이션 대상 리프레시 토큰을 찾을 수 없음: userId={}", userId);
                newQueue.offer(newJwtInformation);
            }

            return newQueue;
        });
    }
}
