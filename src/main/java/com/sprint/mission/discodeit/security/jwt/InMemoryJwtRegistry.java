package com.sprint.mission.discodeit.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    // <userId, Queue<JwtInformation>>
    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        // Queue 없을경우 생성
        origin.computeIfAbsent(userId, k -> new ArrayDeque<>());

        // Queue 가져오기
        Queue<JwtInformation> jwtQueue = origin.get(userId);

        // 최대 동시로그인 수 보다 많으면 가장 처음 등록된 토큰 제거
        if(jwtQueue.size() >= maxActiveJwtCount){
            jwtQueue.poll();
        }
        jwtQueue.offer(jwtInformation);

    }

    @Override
    public void invalidateJwtInformationByUserId(String userId) {
        origin.get(UUID.fromString(userId)).clear();
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(String userId) {
        return Optional.ofNullable(origin.get(UUID.fromString(userId)))
                .map(queue -> {
                    if (queue.peek() != null) {
                        return jwtTokenProvider.verifyAccessToken(queue.peek().getAccessToken());
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {

        UUID userId = UUID.fromString(jwtTokenProvider.extractUserId(accessToken));
        Queue<JwtInformation> jwtQueue = origin.get(userId);

        return jwtQueue.stream()
                .anyMatch(jwtInformation -> jwtInformation.getAccessToken().equals(accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        UUID userId = UUID.fromString(jwtTokenProvider.extractUserId(refreshToken));
        Queue<JwtInformation> jwtQueue = origin.get(userId);

        return jwtQueue.stream()
                .anyMatch(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken));
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        UUID userId = UUID.fromString(jwtTokenProvider.extractUserId(refreshToken));
        Queue<JwtInformation> jwtQueue = origin.get(userId);

        jwtQueue.stream()
                .filter(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(jwtInformation -> {
                    jwtInformation.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                });
    }
}
