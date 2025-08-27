package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        // 쿠키에 담긴 리프레시 토큰으로 무효화
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .ifPresent(c -> jwtRegistry.invalidateByRefreshToken(c.getValue()));
        }

        // 리프레시 토큰 삭제(만료 쿠키로 교체)
        var expireCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(expireCookie);

        SecurityContextHolder.clearContext();

        log.info("로그아웃 처리 완료 - 리프레시 토큰 쿠키 삭제");
    }
}