package com.sprint.mission.discodeit.repository.registry;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();

    private final JwtTokenProvider jwtTokenProvider;
    private final int maxActiveJwtCount = 1;

    @Override
    public void register(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.userDto().id();
        
        log.info("[JwtRegistry] JWT 정보 등록 시작: userId={}", userId);
        
        // 사용자별 Queue 가져오기 (없으면 생성)
        Queue<JwtInformation> userQueue = origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
        
        // 기존 JWT 정보가 있으면 제거 (기존 로그인 무효화)
        if (!userQueue.isEmpty()) {
            JwtInformation existingJwt = userQueue.poll();
            jwtTokenProvider.blacklistAccessToken(existingJwt.accessToken());
            log.info("[JwtRegistry] 기존 로그인 무효화: userId={}", userId);
        }
        
        // 새로운 JWT 정보 추가
        userQueue.offer(jwtInformation);
        
        log.info("[JwtRegistry] JWT 정보 등록 완료: userId={}, 현재 활성 JWT 수={}", 
                userId, userQueue.size());
    }

    @Override
    public void inValidJwtInformationByUserId(UUID userId) {
        log.info("[JwtRegistry] 사용자 JWT 정보 무효화: userId={}", userId);
        
        Queue<JwtInformation> userQueue = origin.get(userId);
        if (userQueue != null) {
            // 사용자의 모든 JWT 정보 제거
            userQueue.forEach(jwt -> {
                jwtTokenProvider.blacklistAccessToken(jwt.accessToken());
                jwtTokenProvider.blacklistRefreshToken(jwt.refreshToken());
            });
            userQueue.clear();
            log.info("[JwtRegistry] 사용자 JWT 정보 모두 제거: userId={}", userId);
        }
        
        // 사용자별 Map에서 제거
        origin.remove(userId);
        
        log.info("[JwtRegistry] 사용자 JWT 정보 무효화 완료: userId={}", userId);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(UUID userId) {
        log.info("[JwtRegistry] 사용자 JWT 정보 확인 시작: userId={}", userId);
        
        Queue<JwtInformation> userQueue = origin.get(userId);
        if (userQueue == null || userQueue.isEmpty()) {
            log.info("[JwtRegistry] 사용자 JWT 정보 없음: userId={}", userId);
            return false;
        }
        
        log.info("[JwtRegistry] 사용자 JWT 정보 발견: userId={}, JWT 개수={}", userId, userQueue.size());
        
        // Queue에서 유효한 JWT 정보가 있는지 확인하고 만료된 토큰 정리
        boolean hasValidJwt = false;
        Queue<JwtInformation> validJwts = new ConcurrentLinkedQueue<>();
        
        for (JwtInformation jwtInfo : userQueue) {
            boolean isValid = jwtTokenProvider.validateAccessToken(jwtInfo.accessToken());
            log.info("[JwtRegistry] JWT 토큰 검증: userId={}, isValid={}, accessToken={}", 
                    userId, isValid, jwtInfo.accessToken().substring(0, 20) + "...");
            
            if (isValid) {
                validJwts.offer(jwtInfo);
                hasValidJwt = true;
            } else {
                log.info("[JwtRegistry] 만료된 JWT 토큰 제거: userId={}, accessToken={}", 
                        userId, jwtInfo.accessToken().substring(0, 20) + "...");
            }
        }
        
        // 유효한 JWT 정보들로 Queue 업데이트 (만료된 것들 제거)
        origin.put(userId, validJwts);
        
        log.info("[JwtRegistry] 사용자 JWT 정보 확인 완료: userId={}, hasValidJwt={}", userId, hasValidJwt);
        return hasValidJwt;
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        // 모든 사용자의 Queue에서 해당 accessToken을 찾기
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userQueue = entry.getValue();
            
            for (JwtInformation jwtInfo : userQueue) {
                if (jwtInfo.accessToken().equals(accessToken)) {
                    // Registry에 등록되어 있고, JWT 검증도 통과하는지 확인
                    boolean isValid = jwtTokenProvider.validateAccessToken(accessToken);
                    
                    // 만료된 토큰이면 제거
                    if (!isValid) {
                        userQueue.remove(jwtInfo);
                        log.info("[JwtRegistry] 만료된 accessToken 제거: userId={}", userId);
                    }
                    
                    return isValid;
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        // 모든 사용자의 Queue에서 해당 refreshToken을 찾기
        for (Map.Entry<UUID, Queue<JwtInformation>> entry : origin.entrySet()) {
            UUID userId = entry.getKey();
            Queue<JwtInformation> userQueue = entry.getValue();
            
            for (JwtInformation jwtInfo : userQueue) {
                if (jwtInfo.refreshToken().equals(refreshToken)) {
                    // Registry에 등록되어 있고, JWT 검증도 통과하는지 확인
                    boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);
                    
                    // 만료된 토큰이면 제거
                    if (!isValid) {
                        userQueue.remove(jwtInfo);
                        log.info("[JwtRegistry] 만료된 refreshToken 제거: userId={}", userId);
                    }
                    
                    return isValid;
                }
            }
        }
        
        return false;
    }

    @Override
    public JwtInformation rotateJwtInformation(String refreshToken, JwtInformation newjwtInformation) {
        log.info("[JwtRegistry] JWT 토큰 로테이션 시작");

        Queue<JwtInformation> jwtInformationQueue = origin.get(newjwtInformation.userDto().id());
        
        if (jwtInformationQueue != null) {
            // 기존 토큰들을 블랙리스트에 추가하고 제거
            jwtInformationQueue.removeIf(jwt -> {
                if (jwt.refreshToken().equals(refreshToken)) {
                    jwtTokenProvider.blacklistAccessToken(jwt.accessToken());
                    jwtTokenProvider.blacklistRefreshToken(jwt.refreshToken());
                    return true; // 제거
                }
                return false; // 유지
            });
        } else {
            // Queue가 없으면 새로 생성
            jwtInformationQueue = new ConcurrentLinkedQueue<>();
        }

        // 새로운 JWT 정보 추가
        jwtInformationQueue.offer(newjwtInformation);
        origin.put(newjwtInformation.userDto().id(), jwtInformationQueue);

        return newjwtInformation;
    }
}
