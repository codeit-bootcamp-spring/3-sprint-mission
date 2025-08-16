package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import com.sprint.mission.discodeit.exception.jwt.InvalidTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    public JwtDto refresh(String refreshToken, HttpServletResponse response) {

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidTokenException(refreshToken);
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(
            username);

        JwtDto jwtDto = null;
        try {
            // 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            JwtInformation newJwtInfo = new JwtInformation(userDetails.getUserResponseDto(),
                newAccessToken, newRefreshToken);

            // Refresh 토큰 Rotation
            JwtInformation jwtInformation = jwtRegistry.rotateJwtInformation(refreshToken,
                newJwtInfo);

            // 리프레시 쿠키 교체
            // HTTP 응답 헤더(Set-Cookie)에 Refresh Cookie를 추가한다.
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            jwtDto = new JwtDto(jwtInformation.userResponseDto(), jwtInformation.accessToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jwtDto;
    }
}
