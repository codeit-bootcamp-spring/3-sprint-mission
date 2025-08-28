package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, JwtInformation> origin = new ConcurrentHashMap<>();
    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userResponseDto().id();
        String username = jwtInformation.userResponseDto().username();

        log.debug("[JwtRegistry] Jwt 정보 등록 시작 username: {}", username);

        origin.compute(userId, (k, oldInfo) -> {
            // 최대 로그인 수 제어
            if (oldInfo != null) {
                removeTokenIndex(oldInfo.accessToken(), oldInfo.refreshToken());
                log.debug("[JwtRegistry] 기존 Jwt 제거- userId:{}, oldJwt: {}", userId, oldInfo);
            }
            // 신규 토큰 인덱스 추가
            addTokenIndex(jwtInformation.accessToken(), jwtInformation.refreshToken());
            log.debug("[JwtRegistry] Jwt 정보 등록 완료 - username: {}",
                username);
            return jwtInformation;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(UUID userId) {
        log.debug("[JwtRegistry] Jwt 정보 삭제 시작 - userId: {}", userId);

        origin.computeIfPresent(userId, (key, info) -> {
            removeTokenIndex(info.accessToken(), info.refreshToken());
            log.debug("[JwtRegistry] Jwt 정보 삭제 완료 - userId: {}", userId);
            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        return origin.containsKey(userId);
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
    public JwtInformation rotateJwtInformation(String refreshToken,
        JwtInformation newJwtInformation) {
        log.debug("[JwtRegistry] Jwt 토큰 로테이션 시작- username: {}",
            newJwtInformation.userResponseDto().username());

        UUID userId = newJwtInformation.userResponseDto().id();
        final JwtInformation[] rotated = {null};

        origin.computeIfPresent(userId, (key, current) -> {

            if (refreshToken.equals(current.refreshToken())) {
                // 기존 인덱스 제거
                removeTokenIndex(current.accessToken(), current.refreshToken());

                current.rotate(newJwtInformation.accessToken(), newJwtInformation.refreshToken());

                addTokenIndex(current.accessToken(), current.refreshToken());

                rotated[0] = current;
            } else {
                log.debug("[JwtRegistry] rotate 실패 - 주어진 refreshToken이 현재와 불일치 userId: {}", userId);
            }
            return current;
        });

        return rotated[0];
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.entrySet().removeIf(entry -> {
            final UUID userId = entry.getKey();
            final JwtInformation info = entry.getValue();

            boolean isExpired = !jwtTokenProvider.validateAccessToken(info.accessToken()) ||
                !jwtTokenProvider.validateRefreshToken(info.refreshToken());

            if (isExpired) {
                removeTokenIndex(info.accessToken(), info.refreshToken());
                log.debug("[JwtRegistry] 만료 Jwt 제거 - userId: {}", userId);
            }
            return isExpired;
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
