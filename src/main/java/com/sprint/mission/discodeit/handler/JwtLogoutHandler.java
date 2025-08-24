package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : JwtLogoutHandler
 * Author       : dounguk
 * Date         : 2025. 8. 22.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {

        // Clear refresh token cookie
        Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
        response.addCookie(refreshTokenExpirationCookie);

        Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
            .findFirst()
            .ifPresent(cookie -> {
                String refreshToken = cookie.getValue();
                UUID userId = tokenProvider.getUserId(refreshToken);
                jwtRegistry.invalidateJwtInformationByUserId(userId);
            });

        log.debug("JWT logout handler executed - refresh token cookie cleared");
    }
}
