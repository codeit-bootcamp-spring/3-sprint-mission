package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtInformation {
    private final UserDto userDto;
    private final String accessToken;
    private final String refreshToken;

    /**
     * 토큰 로테이션 - 새로운 토큰으로 교체
     * */
    public JwtInformation rotate(String newAccessToken, String newRefreshToken) {
        return new JwtInformation(this.userDto, newAccessToken, newRefreshToken);
    }
}
