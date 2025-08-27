package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        // 리프레시 토큰 삭제(만료 쿠키로 교체)
        var expireCookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(expireCookie);

        SecurityContextHolder.clearContext();

        log.info("로그아웃 처리 완료 - 리프레시 토큰 쿠키 삭제");
    }
}