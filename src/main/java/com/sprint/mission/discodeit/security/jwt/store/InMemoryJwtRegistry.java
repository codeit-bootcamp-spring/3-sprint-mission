package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Deque<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        log.info("[JwtRegistry] registerJwtInformation 호출: userId={}, username={}",
            userId, jwtInformation.getUserDto().username());

        origin.compute(userId, (k, dq) -> {
            if (dq == null) {
                dq = new ArrayDeque<>();
            }
            while (dq.size() >= maxActiveJwtCount) {
                dq.pollFirst();
            }
            dq.addLast(jwtInformation);
            return dq;
        });

        log.info("[JwtRegistry] registerJwtInformation 완료: 현재 활성 사용자 수={}", origin.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        origin.remove(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Deque<JwtInformation> dq = origin.get(userId);
        return dq != null && !dq.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByUsername(String username) {
        return origin.values().stream()
            .flatMap(Collection::stream)
            .anyMatch(info -> info.getUserDto().username().equals(username));
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        log.info("[JwtRegistry] hasActiveJwtInformationByAccessToken 호출: 활성 토큰 수={}",
            origin.values().stream().mapToInt(Collection::size).sum());

        boolean result = origin.values().stream()
            .flatMap(Collection::stream)
            .anyMatch(info -> info.getAccessToken().equals(accessToken));

        log.info("[JwtRegistry] hasActiveJwtInformationByAccessToken 결과: {}", result);
        return result;
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        log.info("[JwtRegistry] hasActiveJwtInformationByRefreshToken 호출: 활성 토큰 수={}",
            origin.values().stream().mapToInt(Collection::size).sum());

        boolean result = origin.values().stream()
            .flatMap(Collection::stream)
            .anyMatch(info -> info.getRefreshToken().equals(refreshToken));

        log.info("[JwtRegistry] hasActiveJwtInformationByRefreshToken 결과: {}", result);
        return result;
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.forEach((userId, queue) -> {
            for (JwtInformation info : queue) {
                if (info.getRefreshToken().equals(refreshToken)) {
                    info.rotate(newJwtInformation.getAccessToken(),
                        newJwtInformation.getRefreshToken());
                }
            }
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        log.info("[JwtRegistry] 만료된 토큰 정보 정리 시작");

        int removedCount = 0;
        Date now = new Date();

        for (Map.Entry<UUID, Deque<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Deque<JwtInformation> queue = entry.getValue();

            // 만료된 토큰들을 찾아서 제거
            queue.removeIf(jwtInfo -> {
                try {
                    // Access Token과 Refresh Token 중 하나라도 만료되었으면 제거
                    Date accessExpiry = jwtTokenProvider.getExpiration(jwtInfo.getAccessToken());
                    Date refreshExpiry = jwtTokenProvider.getExpiration(jwtInfo.getRefreshToken());

                    boolean isExpired = (accessExpiry != null && accessExpiry.before(now)) ||
                        (refreshExpiry != null && refreshExpiry.before(now));

                    if (isExpired) {
                        log.debug("[JwtRegistry] 만료된 토큰 정보 제거: userId={}", userId);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    // 토큰 파싱 오류가 발생한 경우에도 제거
                    log.warn("[JwtRegistry] 토큰 파싱 오류로 인한 제거: userId={}, error={}",
                        userId, e.getMessage());
                    return true;
                }
            });

            // 빈 큐는 맵에서 제거
            if (queue.isEmpty()) {
                origin.remove(userId);
                removedCount++;
            }
        }

        log.info("[JwtRegistry] 만료된 토큰 정리 완료: 제거된 사용자 수={}, 현재 활성 사용자 수={}",
            removedCount, origin.size());
    }
}
