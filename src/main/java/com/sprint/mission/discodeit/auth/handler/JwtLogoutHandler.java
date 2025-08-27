package com.sprint.mission.discodeit.auth.handler;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
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
@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        log.info("[JwtLogoutHandler] 로그아웃 요청 처리 시작");

        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(cookie -> {
                    String refreshToken = cookie.getValue();

                    if (StringUtils.hasText(refreshToken) && tokenProvider.validateRefreshToken(refreshToken)) {
                        UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
                        jwtRegistry.invalidateJwtInformationByUserId(userId);
                        log.debug("[JwtLogoutHandler] Registry에서 토큰 무효화 완료 - userId={}", userId);
                    }
                });
        }

        tokenProvider.expireRefreshCookie(response);
        SecurityContextHolder.clearContext();

        log.info("[JwtLogoutHandler] 로그아웃 처리 완료");
    }
}
