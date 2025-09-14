package com.sprint.mission.discodeit.security.jwt.store;

import java.util.Optional;
import java.util.UUID;

/**
 * JWT 토큰의 상태를 관리하는 레지스트리 인터페이스입니다.
 * 
 * <p>토큰 기반 인증 방식은 세션 기반 인증 방식과 달리 무상태(stateless)이기 때문에 
 * 사용자의 로그인 상태를 제어하기 어렵습니다. 이 인터페이스를 통해 JWT의 상태를 
 * 관리할 수 있습니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>토큰 정보 등록 및 동시 로그인 제한</li>
 *   <li>사용자별 토큰 무효화</li>
 *   <li>토큰 존재 여부 확인</li>
 *   <li>토큰 로테이션</li>
 *   <li>만료된 토큰 정리</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public interface JwtRegistry {

    /**
     * 로그인 성공 시 JWT 정보를 등록합니다.
     * 
     * <p>최대 동시 로그인 수를 제어하여, 제한을 초과하는 경우 
     * 오래된 토큰을 자동으로 제거합니다.</p>
     * 
     * @param jwtInformation 등록할 JWT 정보
     */
    void registerJwtInformation(JwtInformation jwtInformation);

    /**
     * 사용자 ID로 해당 유저의 모든 JWT 정보를 무효화합니다.
     * 
     * <p>로그아웃이나 계정 정지 등의 상황에서 사용됩니다.</p>
     * 
     * @param userId 무효화할 사용자의 ID
     */
    void invalidateJwtInformationByUserId(UUID userId);

    /**
     * 사용자 ID로 활성 JWT 정보가 존재하는지 확인합니다.
     * 
     * <p>사용자의 로그인 상태를 판단할 때 활용됩니다.</p>
     * 
     * @param userId 확인할 사용자의 ID
     * @return 활성 JWT 정보가 존재하면 true, 그렇지 않으면 false
     */
    boolean hasActiveJwtInformationByUserId(UUID userId);

    /**
     * Access Token으로 활성 JWT 정보가 존재하는지 확인합니다.
     * 
     * <p>필터에서 유효한 토큰인지 확인할 때 활용됩니다.</p>
     * 
     * @param accessToken 확인할 Access Token
     * @return 활성 JWT 정보가 존재하면 true, 그렇지 않으면 false
     */
    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    /**
     * Refresh Token으로 활성 JWT 정보가 존재하는지 확인합니다.
     * 
     * <p>토큰 재발급 시 유효한 토큰인지 확인할 때 활용됩니다.</p>
     * 
     * @param refreshToken 확인할 Refresh Token
     * @return 활성 JWT 정보가 존재하면 true, 그렇지 않으면 false
     */
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    /**
     * 토큰 재발급 시 토큰 로테이션을 수행합니다.
     * 
     * <p>기존 Refresh Token을 새로운 JWT 정보로 교체합니다.</p>
     * 
     * @param refreshToken 교체할 기존 Refresh Token
     * @param newJwtInformation 새로운 JWT 정보
     */
    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    /**
     * 만료된 JWT 정보를 정리합니다.
     * 
     * <p>정기적으로 호출하여 만료된 토큰을 메모리에서 제거합니다.</p>
     */
    void clearExpiredJwtInformation();

    UUID findUserIdByRefreshToken(String refreshToken);
}
