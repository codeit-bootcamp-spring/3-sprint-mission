package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.dto.data.UserDto;


public record JwtInformation (

        UserDto userDto,

        String accessToken,

        String refreshToken
) {


        public static void rotate(String accessToken, String refreshToken) {

        // 기존 토큰 무효화(revoked)

        // 기존 토큰의 replacedBy에 새 토큰 jti 입력

        // 새 토큰 발급
    }
}
