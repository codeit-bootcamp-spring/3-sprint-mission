package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
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

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount;

    public InMemoryJwtRegistry(@Value("${jwt.max-concurrent-sessions:1}") int maxActiveJwtCount) {
        this.maxActiveJwtCount = maxActiveJwtCount;
    }

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();
        String username = jwtInformation.userDto().username();

        log.debug("[JwtRegistry] JWT 정보 등록 시작 - 사용자: {}", username);

        Queue<JwtInformation> userJwts = origin.computeIfAbsent(userId, k -> new LinkedList<>());

        while (userJwts.size() >= maxActiveJwtCount) {
            JwtInformation removed = userJwts.poll();
            log.debug("[JwtRegistry] 최대 로그인 수 초과로 기존 JWT 정보 제거 - 사용자: {}", username);
        }

        userJwts.offer(jwtInformation);
        log.debug("[JwtRegistry] JWT 정보 등록 완료 - 사용자: {}, 현재 활성 JWT 수: {}",
            username, userJwts.size());
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {

        log.debug("[JwtRegistry] 사용자 ID로 JWT 정보 무효화 - userId: {}", userId);

        Queue<JwtInformation> removed = origin.remove(userId);

        if (removed != null) {
            log.debug("[JwtRegistry] JWT 정보 무효화 완료 - userId: {}, 제거된 JWT 수: {}",
                userId, removed.size());
        } else {
            log.debug("[JwtRegistry] 무효화할 JWT 정보가 없음 - userId: {}", userId);
        }

    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {

        boolean hasActive = origin.containsKey(userId) && !origin.get(userId).isEmpty();

        log.debug("[JwtRegistry] 사용자 ID로 활성 JWT 확인 - userId: {}, 활성: {}", userId, hasActive);

        return hasActive;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {

        boolean hasActive = origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(jwt -> accessToken.equals(jwt.accessToken()));

        log.debug("[JwtRegistry] 액세스 토큰으로 활성 JWT 확인 - 활성: {}", hasActive);

        return hasActive;
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {

        boolean hasActive = origin.values().stream()
            .flatMap(Queue::stream)
            .anyMatch(jwt -> refreshToken.equals(jwt.refreshToken()));

        log.debug("[JwtRegistry] 리프레시 토큰으로 활성 JWT 확인 - 활성: {}", hasActive);
        return hasActive;

    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {

        UUID userId = newJwtInformation.userDto().id();
        String username = newJwtInformation.userDto().username();

        log.debug("[JwtRegistry] JWT 정보 로테이션 시작 - 사용자: {}", username);

        Queue<JwtInformation> userJwts = origin.get(userId);
        if (userJwts != null) {
            Queue<JwtInformation> updatedJwts = new LinkedList<>();
            boolean rotated = false;

            for (JwtInformation jwt : userJwts) {
                if (refreshToken.equals(jwt.refreshToken()) && !rotated) {
                    updatedJwts.offer(newJwtInformation);
                    rotated = true;
                    log.debug("[JwtRegistry] JWT 정보 로테이션 완료 - 사용자: {}", username);
                } else {
                    updatedJwts.offer(jwt);
                }
            }

            origin.put(userId, updatedJwts);

            if (!rotated) {
                log.warn("[JwtRegistry] 로테이션 대상 JWT 정보를 찾을 수 없음 - 사용자: {}", username);
            }
        } else {
            log.warn("[JwtRegistry] 사용자의 JWT 정보가 존재하지 않음 - 사용자: {}", username);
        }


    }
}
