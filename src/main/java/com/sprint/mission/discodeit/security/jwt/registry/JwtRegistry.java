package com.sprint.mission.discodeit.security.jwt.registry;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.Optional;
import java.util.UUID;

public interface JwtRegistry {

    void registerJwtInformation(JwtInformation info);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    /** Refresh Rotation: 기존 refreshToken 기반으로 새 JwtInformation으로 교체 */
    boolean rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo);

    /** 로그아웃 등에서 refreshToken으로 무효화 */
    void invalidateByRefreshToken(String refreshToken);

    /** 주기적으로 만료 토큰 청소 */
    void clearExpiredJwtInformation();

    /** 필터 등에서 부가 검사용 */
    Optional<JwtInformation> findByAccessToken(String accessToken);
    Optional<JwtInformation> findByRefreshToken(String refreshToken);
}
