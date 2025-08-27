package com.sprint.mission.discodeit.security.jwt.registry;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
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

    // <userId, Queue<JwtInformation>>
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

    // 빠른 역참조 인덱스
    private final Map<String, JwtInformation> accessIndex = new ConcurrentHashMap<>();
    private final Map<String, JwtInformation> refreshIndex = new ConcurrentHashMap<>();

    private final int maxActiveJwtCount;

    public InMemoryJwtRegistry(
        @Value("${discodeit.jwt.registry.max-active:1}") int maxActiveJwtCount
    ) {
        this.maxActiveJwtCount = Math.max(1, maxActiveJwtCount);
    }

    @Override
    public void registerJwtInformation(JwtInformation info) {
        origin.compute(info.userId(), (uid, q) -> {
            if (q == null) q = new ConcurrentLinkedQueue<>();
            q.add(info);
            // 동시 로그인 제한(기본 1개) – 오래된 것부터 제거
            while (q.size() > maxActiveJwtCount) {
                JwtInformation old = q.poll();
                if (old != null) removeIndexes(old);
            }
            return q;
        });
        putIndexes(info);
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> q = origin.remove(userId);
        if (q != null) {
            q.forEach(this::removeIndexes);
            q.clear();
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> q = origin.get(userId);
        if (q == null || q.isEmpty()) return false;
        return q.stream().anyMatch(this::notExpired);
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        JwtInformation info = accessIndex.get(accessToken);
        return info != null && notExpired(info);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        JwtInformation info = refreshIndex.get(refreshToken);
        return info != null && notExpired(info);
    }

    @Override
    public boolean rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo) {
        JwtInformation old = refreshIndex.get(oldRefreshToken);
        if (old == null) return false;
        // 동일 사용자에 대해서만 회전 허용
        if (!old.userId().equals(newInfo.userId())) {
            return false;
        }
        return origin.computeIfPresent(old.userId(), (uid, q) -> {
            q.remove(old);
            removeIndexes(old);
            q.add(newInfo);
            putIndexes(newInfo);
            return q;
        }) != null;
    }

    @Override
    public void invalidateByRefreshToken(String refreshToken) {
        JwtInformation old = refreshIndex.remove(refreshToken);
        if (old == null) return;
        accessIndex.remove(old.accessToken());
        origin.computeIfPresent(old.userId(), (uid, q) -> { q.remove(old); return q; });
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void clearExpiredJwtInformation() {
        Instant now = Instant.now();
        origin.entrySet().removeIf(entry -> {
            UUID uid = entry.getKey();
            Queue<JwtInformation> q = entry.getValue();

            q.removeIf(info -> {
                boolean expired = info.refreshTokenExpiresAt().isBefore(now);
                if (expired) removeIndexes(info);
                return expired;
            });
            boolean empty = q.isEmpty();
            if (empty) {
                log.trace("JWT 레지스트리 정리: userId={}의 항목이 비어 제거됨", uid);
            }
            return empty;
        });
        log.debug("만료 토큰 정리 완료");
    }

    @Override
    public Optional<JwtInformation> findByAccessToken(String accessToken) {
        return Optional.ofNullable(accessIndex.get(accessToken));
    }

    @Override
    public Optional<JwtInformation> findByRefreshToken(String refreshToken) {
        return Optional.ofNullable(refreshIndex.get(refreshToken));
    }

    // ===== 내부 유틸 =====
    private void putIndexes(JwtInformation info) {
        accessIndex.put(info.accessToken(), info);
        refreshIndex.put(info.refreshToken(), info);
    }

    private void removeIndexes(JwtInformation info) {
        accessIndex.remove(info.accessToken());
        refreshIndex.remove(info.refreshToken());
    }

    private boolean notExpired(JwtInformation info) {
        return Instant.now().isBefore(info.refreshTokenExpiresAt());
    }
}
