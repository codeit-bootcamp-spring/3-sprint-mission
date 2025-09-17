package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

//@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

    @Value("${jwt.max-active-count:1}")
    private int maxActiveJwtCount;

    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<UUID, Deque<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        log.info("[JwtRegistry] registerJwtInformation 호출: userId={}, username={}",
            userId, jwtInformation.getUserDto().username());

        origin.compute(userId, (k, dq) -> {
            if (dq == null) {
                dq = new ArrayDeque<>();
            }

            // 오래된 토큰 제거
            while (dq.size() >= maxActiveJwtCount) {
                JwtInformation removed = dq.pollFirst();
                if (removed != null) {
                    accessTokenIndexes.remove(removed.getAccessToken());
                    refreshTokenIndexes.remove(removed.getRefreshToken());
                }
            }
            dq.addLast(jwtInformation);
            return dq;
        });

        accessTokenIndexes.add(jwtInformation.getAccessToken());
        refreshTokenIndexes.add(jwtInformation.getRefreshToken());
        eventPublisher.publishEvent(new UserLogInOutEvent(jwtInformation.getUserDto().id(), true));
        log.info("[JwtRegistry] UserLogInOutEvent 발행됨: {}", userId);

        log.info("[JwtRegistry] registerJwtInformation 완료: 현재 활성 사용자 수={}", origin.size());
    }

    @CacheEvict(value = "users", key = "'all'")
    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.warn("[JwtRegistry] invalidate 호출됨! userId={}, caller={}",
            userId, Arrays.toString(Thread.currentThread().getStackTrace()));
        Deque<JwtInformation> infos = origin.remove(userId);
        if (infos != null) {
            infos.forEach(info -> {
                accessTokenIndexes.remove(info.getAccessToken());
                refreshTokenIndexes.remove(info.getRefreshToken());
            });
        }
        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
        log.info("[JwtRegistry] UserLogInOutEvent 발행됨: {}", userId);
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
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Deque<JwtInformation> infos = origin.get(userId);
        if (infos == null || infos.isEmpty()) {
            return false;
        }

        Date now = new Date();
        return infos.stream().anyMatch(info -> {
            try {
                Date accessExpiry = jwtTokenProvider.getExpiration(info.getAccessToken());
                Date refreshExpiry = jwtTokenProvider.getExpiration(info.getRefreshToken());
                return (accessExpiry == null || accessExpiry.after(now))
                    && (refreshExpiry == null || refreshExpiry.after(now));
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.forEach((userId, queue) -> {
            for (JwtInformation info : queue) {
                if (info.getRefreshToken().equals(refreshToken)) {
                    // 인덱스에서 기존 토큰 제거
                    accessTokenIndexes.remove(info.getAccessToken());
                    refreshTokenIndexes.remove(info.getRefreshToken());

                    info.rotate(newJwtInformation.getAccessToken(),
                        newJwtInformation.getRefreshToken());

                    // 인덱스에 새 토큰 등록
                    accessTokenIndexes.add(info.getAccessToken());
                    refreshTokenIndexes.add(info.getRefreshToken());
                    return;
                }
            }
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        log.info("[JwtRegistry] 만료된 토큰 정보 정리 시작");
        Date now = new Date();
        int removedCount = 0;

        for (Map.Entry<UUID, Deque<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Deque<JwtInformation> queue = entry.getValue();

            queue.removeIf(jwtInfo -> {
                try {
                    Date accessExpiry = jwtTokenProvider.getExpiration(jwtInfo.getAccessToken());
                    Date refreshExpiry = jwtTokenProvider.getExpiration(jwtInfo.getRefreshToken());

                    boolean isExpired = (accessExpiry != null && accessExpiry.before(now))
                        || (refreshExpiry != null && refreshExpiry.before(now));

                    if (isExpired) {
                        accessTokenIndexes.remove(jwtInfo.getAccessToken());
                        refreshTokenIndexes.remove(jwtInfo.getRefreshToken());
                        log.debug("[JwtRegistry] 만료 토큰 제거: userId={}", userId);
                        eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    accessTokenIndexes.remove(jwtInfo.getAccessToken());
                    refreshTokenIndexes.remove(jwtInfo.getRefreshToken());
                    log.warn("[JwtRegistry] 파싱 오류로 제거: userId={}, error={}", userId,
                        e.getMessage());
                    return true;
                }
            });

            if (queue.isEmpty()) {
                origin.remove(userId);
                removedCount++;
            }
        }
        log.info("[JwtRegistry] 만료 토큰 정리 완료: 제거된 사용자 수={}, 현재 활성 사용자 수={}",
            removedCount, origin.size());
    }
}
