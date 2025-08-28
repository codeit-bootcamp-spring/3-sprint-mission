package com.sprint.mission.discodeit.dto.jwt;

import java.util.UUID;

/**
 * 서버에서 JWT를 관리하기 위한 내부 저장 객체
 *
 * @param userId       사용자 ID
 * @param username     토큰과 연결된 계정명
 * @param accessToken  인증 요청 시 사용할 액세스 토큰
 * @param refreshToken AccessToken 재발급 시 사용하는 토큰
 */
public record JwtInformation(
    UUID userId,
    String username,
    String accessToken,
    String refreshToken
) {

    public JwtInformation rotate(String newAccessToken, String newRefreshToken) {
        return new JwtInformation(userId, username, newAccessToken, newRefreshToken);
    }
}
