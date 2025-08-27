package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final JwtTokenProvider tokenProvider;

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();

        log.debug("[JwtRegistry] JWT 정보 등록 시작: userId={}", userId);

        origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
        Queue<JwtInformation> queue = origin.get(userId);

        while (!queue.isEmpty()) {
            JwtInformation removed = queue.poll();
            log.debug("[InMemoryJwtRegistry] 동시 로그인 초과 → 기존 토큰 제거: userId={}, removedAT={}", userId, removed.accessToken());
        }

        queue.add(jwtInformation);

        log.debug("[InMemoryJwtRegistry] 토큰 등록 완료: userId={}, activeCount={}", userId, queue.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.debug("[InMemoryJwtRegistry] 토큰 무효화: userId={}", userId);

        Queue<JwtInformation> removed = origin.remove(userId);

        if (removed != null) {
            log.debug("[JwtRegistry] JWT 정보 무효화 완료: userId={}, 제거된 JWT 수={}", userId, removed.size());
        } else {
            log.debug("[JwtRegistry] 무효화할 JWT 정보가 없음: userId={}", userId);
        }
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId) && !origin.get(userId).isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(info -> info.accessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(info -> info.refreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        log.debug("[JwtRegistry] JWT 정보 로테이션 시작: 사용자={}", newJwtInformation.userDto().username());

        origin.values().forEach(queue -> {
            queue.stream()
                .filter(info -> info.refreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(oldInfo -> {
                    JwtInformation rotated = oldInfo.rotate(
                        newJwtInformation.accessToken(),
                        newJwtInformation.refreshToken()
                    );

                    queue.remove(oldInfo);
                    queue.add(rotated);

                    log.debug("[InMemoryJwtRegistry] 토큰 회전 완료 - userId={}", rotated.userDto().id());
                });
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        log.info("[InMemoryJwtRegistry] 만료된 JWT 정리 작업 시작");

        origin.forEach((userId, queue) -> {
            boolean removed = queue.removeIf(info ->
                !tokenProvider.validateAccessToken(info.accessToken()) &&
                    !tokenProvider.validateRefreshToken(info.refreshToken())
            );
            if (removed) {
                log.debug("[InMemoryJwtRegistry] 만료 토큰 제거 완료: userId={}", userId);
            }
        });

        log.info("[InMemoryJwtRegistry] 만료된 JWT 정리 작업 완료");
    }
}