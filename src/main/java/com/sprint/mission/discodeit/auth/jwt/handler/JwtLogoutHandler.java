package com.sprint.mission.discodeit.auth.jwt.handler;

import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.auth.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        log.info("[JwtLogoutHandler] 로그아웃 처리 시작");
        try {
            boolean invalidated = false;

            // 레지스트리에서 사용자의 활성 JWT 무효화 (인증된 경우)
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
                UUID userId = userDetails.getId();
                jwtRegistry.invalidateJwtInformationByUserId(userId);
                log.debug("[JwtLogoutHandler] 레지스트리 무효화 완료 - userId={}", userId);
                invalidated = true;
            }

            // 인증 없어도 요청 쿠키의 리프레시 토큰을 활용해 토큰을 무효화
            String refreshToken = extractRefreshTokenFromCookie(request);
            if (!invalidated && StringUtils.hasText(refreshToken)) {
                if (jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
                    // 단일 세션 제거
                    jwtRegistry.invalidateByRefreshToken(refreshToken);

                    log.debug("[JwtLogoutHandler] 쿠키 RT 기반 세션 무효화 완료");
                    invalidated = true;
                }
            }

            if (!invalidated) {
                log.debug("[JwtLogoutHandler] 무효화할 활성 세션 없음");
            }

        } catch (Exception e) {
            log.warn("[JwtLogoutHandler] 처리 중 예외: {}", e.getMessage(), e);
        } finally {
            tokenProvider.expireRefreshCookie(response);
            SecurityContextHolder.clearContext();
            log.debug("[JwtLogoutHandler] 리프레시 토큰 쿠키 만료, SecurityContext 초기화");
        }
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }
}
