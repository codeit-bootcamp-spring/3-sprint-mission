package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
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
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> usernameIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();
        String username = jwtInformation.getUserDto().username();

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

                    if (dq.isEmpty()) {
                        usernameIndexes.remove(removed.getUserDto().username());
                    }
                }
            }
            dq.addLast(jwtInformation);
            return dq;
        });

        accessTokenIndexes.add(jwtInformation.getAccessToken());
        refreshTokenIndexes.add(jwtInformation.getRefreshToken());
        usernameIndexes.add(username);

        log.info("[JwtRegistry] registerJwtInformation 완료: 현재 활성 사용자 수={}", origin.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        Deque<JwtInformation> infos = origin.remove(userId);
        if (infos != null) {
            infos.forEach(info -> {
                accessTokenIndexes.remove(info.getAccessToken());
                refreshTokenIndexes.remove(info.getRefreshToken());
            });
        }
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
    public boolean hasActiveJwtInformationByUsername(String username) {
        return usernameIndexes.contains(username);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Deque<JwtInformation> infos = origin.get(userId);
        return infos != null && !infos.isEmpty();
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
                    usernameIndexes.add(info.getUserDto().username());
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
                        if (queue.size() == 1) {
                            usernameIndexes.remove(jwtInfo.getUserDto().username());
                        }
                        log.debug("[JwtRegistry] 만료 토큰 제거: userId={}", userId);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    accessTokenIndexes.remove(jwtInfo.getAccessToken());
                    refreshTokenIndexes.remove(jwtInfo.getRefreshToken());
                    usernameIndexes.remove(jwtInfo.getUserDto().username());
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
