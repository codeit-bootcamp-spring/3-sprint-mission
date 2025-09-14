package com.sprint.mission.discodeit.auth.jwt.registry;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    // 사용자별 활성 JWT 큐
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    // 동시 로그인 허용 개수
    private final int maxActiveJwtCount;

    public InMemoryJwtRegistry(@Value("${jwt.max-concurrent-sessions:1}") int maxActiveJwtCount) {
        this.maxActiveJwtCount = maxActiveJwtCount;
    }

    @Override
    public void registerJwt(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        String username = jwtInformation.getUserDto().username();
        log.info("[JwtRegistry] 등록 시작 - user: {}, userId: {}", username, userId);

        Queue<JwtInformation> queue = origin.computeIfAbsent(userId, k -> new LinkedList<>());

        // 동일 RT가 이미 있으면 제거
        queue.removeIf(existing ->
            jwtInformation.getRefreshToken().equals(existing.getRefreshToken()));

        // 동시 로그인 제한 (오래된 것부터 없애기)
        while (queue.size() >= maxActiveJwtCount) {
            JwtInformation removed = queue.poll();
            if (removed != null) {
                log.debug("[JwtRegistry] 동시 로그인 제한으로 제거 - user: {}", username);
            }
        }

        queue.offer(jwtInformation);
        log.debug("[JwtRegistry] 등록 완료 - user: {}, activeCount: {}", username, queue.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.remove(userId);

        if (queue != null) {
            log.debug("[JwtRegistry] JWT 정보 무효화 완료 - userId: {}, 제거된 JWT 수: {}",
                userId, queue.size());
        } else {
            log.debug("[JwtRegistry] 무효화할 JWT 정보가 없음 - userId: {}", userId);
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> queue = origin.get(userId);
        return queue != null && !queue.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        if (accessToken == null) return false;

        for (Queue<JwtInformation> q : origin.values()) {
            for (JwtInformation info : q) {
                if (accessToken.equals(info.getAccessToken())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        if (refreshToken == null) return false;
        for (Queue<JwtInformation> q : origin.values()) {
            for (JwtInformation info : q) {
                if (refreshToken.equals(info.getRefreshToken())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        if (refreshToken == null || newJwtInformation == null) return;

        UUID userId = newJwtInformation.getUserDto().id();
        Queue<JwtInformation> queue = origin.get(userId);
        if (queue == null) return;
        log.debug("[JwtRegistry] 로테이션 시작 - userId={}", userId);

        for (JwtInformation info : queue) {
            if (refreshToken.equals(info.getRefreshToken())) {
                info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                log.debug("[JwtRegistry] 로테이션 완료 - userId={}", userId);
                return;
            }
        }
    }

    @Override
    public void invalidateByRefreshToken(String refreshToken) {
        if (refreshToken == null) return;
        for (Map.Entry<UUID, Queue<JwtInformation>> e : origin.entrySet()) {
            Queue<JwtInformation> q = e.getValue();
            if (q == null) continue;
            boolean removed = q.removeIf(info -> refreshToken.equals(info.getRefreshToken()));
            if (removed && q.isEmpty()) {
                origin.remove(e.getKey());
            }
            if (removed) return;
        }
    }
}
