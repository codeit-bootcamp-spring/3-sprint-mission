package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {

    /**
     * JWT 정보 등록 ( 로그인 성공 시 )
     * 최대 동시 로그인 수를 제어
     * */
    void registerJwtInformation(JwtInformation jwtInformation);

    /**
     * 사용자 ID로 모든 JWT 정보 무효화
     * */
    void invalidateJwtInformationByUserId(UUID userId);

    /**
     * 사용자 ID로 활성 JWT 정보 존재 여부 확인
     * */
    boolean hasActiveJwtInformationByUserId(UUID userId);

    /**
     * 액세스 토큰으로 활성 JWT 정보 존재 여부 확인
     * */
    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    /**
     * 리프레시 토큰으로 활성 JWT 정보 존재 여부 확인
     * */
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    /**
     * 토큰 로테이션 수행 ( 토큰 재발급 시 )
     * */
    void rotateJwtInformation(String refreshToken,JwtInformation jwtInformation);
}
