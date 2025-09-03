package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.auth.RefreshTokenNotFoundException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final DiscodeitUserDetailsService userDetailsService;

    public JwtDto refreshTokens(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        // 1. 토큰 검증
        validateRefreshToken(refreshToken);

        // 2. 사용자 정보 조회
        DiscodeitUserDetails userDetails = getUserDetailsFromToken(refreshToken);

        // 3. 새로운 토큰 생성 및 로테이션
        TokenPair newTokens = generateAndRotateTokens(refreshToken, userDetails);

        // 4. 쿠키 설정
        setRefreshTokenCookie(response, newTokens.refreshToken(), request);

        // 5. 응답 DTO 생성
        JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), newTokens.accessToken());

        log.info("토큰 재발급 완료: userId = {}", userDetails.getUserDto().id());

        return jwtDto;
    }

    private void validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new RefreshTokenNotFoundException();
        }

        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new InvalidRefreshTokenException();
        }
    }

    private DiscodeitUserDetails getUserDetailsFromToken(String refreshToken) {
        String username = jwtTokenProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return (DiscodeitUserDetails) userDetails;
    }

    private TokenPair generateAndRotateTokens(String oldRefreshToken, DiscodeitUserDetails userDetails) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        JwtInformation newJwtInformation = new JwtInformation(
            userDetails.getUserDto(), newAccessToken, newRefreshToken
        );

        jwtRegistry.rotateJwtInformation(oldRefreshToken, newJwtInformation);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, HttpServletRequest request) {
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(request.isSecure());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) jwtTokenProvider.getRefreshTokenValidityInSeconds());
        response.addCookie(refreshTokenCookie);
    }

    private record TokenPair(String accessToken, String refreshToken) {}
}
