package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int MAX_ACTIVE_JWT_COUNT = 1;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userResponseDto().id();
        String username = jwtInformation.userResponseDto().username();

        log.debug("[JwtRegistry] Jwt 정보 등록 시작 username: {}", username);

        origin.compute(userId, (k, q) -> {
            if (q == null) {
                q = new LinkedList<>();
            }
            // 최대 로그인 수 제어
            while (q.size() >= MAX_ACTIVE_JWT_COUNT) {
                q.poll();
            }
            q.add(jwtInformation);
            log.debug("[JwtRegistry] Jwt 정보 등록 완료 - username: {}, 활성화된 Jwt 수: {}",
                username, q.size());
            return q;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.debug("[JwtRegistry] Jwt 정보 삭제 시작 - userId: {}", userId);

        Queue<JwtInformation> invalidated = origin.remove(userId);

        if (invalidated != null) {
            log.debug("[JwtRegistry] Jwt 정보 삭제 완료 - userId: {}, 삭제된 Jwt 수: {}",
                userId, invalidated.size());
        } else {
            log.debug("[JwtRegistry] 삭제된 Jwt 정보 없음 - userId: {}", userId);
        }
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
        Queue<JwtInformation> q = origin.get(userId);

        if (q == null || q.isEmpty()) {
            log.debug("[JwtRegistry] 활성 토큰 없음 - userId: {}", userId);
            return null;
        }

        for (JwtInformation cur : q) {
            if (refreshToken.equals(cur.refreshToken())) {
                JwtInformation rotated = cur.rotate(
                    newJwtInformation.accessToken(),
                    newJwtInformation.refreshToken()
                );

                // 기존 항목 제거 후 최신 항목으로 교체
                q.remove(cur);
                while (q.size() >= MAX_ACTIVE_JWT_COUNT) {
                    q.poll();
                }
                q.add(rotated);

                log.debug("[JwtRegistry] 토큰 로테이션 완료 - userId: {}", userId);
                return rotated;
            }
        }

        log.debug("[JwtRegistry] Refresh 토큰 없음 - userId: {}", userId);
        return null;
    }
}
