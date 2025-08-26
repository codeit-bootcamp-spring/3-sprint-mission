package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();
    private final int MAX_ACTIVE_JWT_COUNT = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userResponseDto().id();
        String username = jwtInformation.userResponseDto().username();

        log.debug("[JwtRegistry] Jwt 정보 등록 시작 username: {}", username);

        origin.compute(userId, (k, q) -> {
            if (q == null) {
                q = new ConcurrentLinkedQueue<>();
            }
            // 최대 로그인 수 제어
            if (q.size() >= MAX_ACTIVE_JWT_COUNT) {
                JwtInformation deprecatedJwtInformation = q.poll();
                if (deprecatedJwtInformation != null) {
                    log.debug("[JwtRegistry] 제거된 JwtInformation: {}", deprecatedJwtInformation);
                    removeTokenIndex(deprecatedJwtInformation.accessToken(),
                        deprecatedJwtInformation.refreshToken());
                }
            }
            q.add(jwtInformation);
            addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());
            log.debug("[JwtRegistry] Jwt 정보 등록 완료 - username: {}, 활성화된 Jwt 수: {}",
                username, q.size());
            return q;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.debug("[JwtRegistry] Jwt 정보 삭제 시작 - userId: {}", userId);

        origin.computeIfPresent(userId, (key, q) -> {
            q.forEach(jwtInformation -> {
                removeTokenIndex(
                    jwtInformation.accessToken(),
                    jwtInformation.refreshToken()
                );
            });
            q.clear();
            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        Queue<JwtInformation> q = origin.get(userId);

        return q != null && !q.isEmpty();
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return origin.values().stream()
            .flatMap(Collection::stream)
            .anyMatch(info -> accessToken.equals(info.accessToken()));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return origin.values().stream()
            .flatMap(Collection::stream)
            .anyMatch(info -> refreshToken.equals(info.refreshToken()));
    }

    @Override
    public JwtInformation rotateJwtInformation(String refreshToken,
        JwtInformation newJwtInformation) {
        log.debug("[JwtRegistry] Jwt 토큰 로테이션 시작- username: {}",
            newJwtInformation.userResponseDto().username());

        UUID userId = newJwtInformation.userResponseDto().id();
        final JwtInformation[] rotated = {null};

        origin.computeIfPresent(userId, (key, q) -> {
            if (q.isEmpty()) {
                log.debug("[JwtRegistry] 활성 토큰 없음 - userId: {}", userId);
                return q; // null 반환하면 entry 삭제되므로 주의
            }

            q.stream()
                .filter(jwtInformation -> jwtInformation.refreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(jwtInformation -> {
                    removeTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());
                    jwtInformation.rotate(newJwtInformation.accessToken(),
                        newJwtInformation.refreshToken());
                    addTokenIndex(newJwtInformation.accessToken(),
                        newJwtInformation.refreshToken());
                    rotated[0] = jwtInformation;
                });

            return q; // 반드시 Queue 반환
        });

        return rotated[0]; // 최종적으로 JwtInformation 반환
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(jwtInformation -> {
                boolean isExpired =
                    !jwtTokenProvider.validateAccessToken(jwtInformation.accessToken()) ||
                        !jwtTokenProvider.validateRefreshToken(jwtInformation.refreshToken());
                if (isExpired) {
                    removeTokenIndex(
                        jwtInformation.accessToken(),
                        jwtInformation.refreshToken()
                    );
                }
                return isExpired;
            });
            return queue.isEmpty(); // Remove the entry if the queue is empty
        });
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
