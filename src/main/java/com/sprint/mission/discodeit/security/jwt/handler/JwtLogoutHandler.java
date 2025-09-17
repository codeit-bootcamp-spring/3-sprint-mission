package com.sprint.mission.discodeit.security.jwt.handler;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        log.info("[JwtLogoutHandler] 로그아웃 처리 시작");

        // 인증된 사용자의 경우 해당 사용자의 모든 토큰 무효화
        if (authentication != null
            && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            log.info("[JwtLogoutHandler] 인증된 사용자 토큰 무효화: username={}", userDetails.getUsername());
            jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().id());
        } else {
            // 인증 정보가 없는 경우 쿠키의 리프레시 토큰을 통해 처리
            if (request.getCookies() != null) {
                Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName()
                        .equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .ifPresent(cookie -> {
                        try {
                            String refreshToken = cookie.getValue();
                            if (tokenProvider.validateRefreshToken(refreshToken)) {
                                String username = tokenProvider.getUsernameFromToken(refreshToken);
                                log.info("[JwtLogoutHandler] 쿠키 리프레시 토큰으로 사용자 식별: username={}",
                                    username);
                                UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
                                jwtRegistry.invalidateJwtInformationByUserId(userId);
                            }
                        } catch (Exception e) {
                            log.warn("[JwtLogoutHandler] 쿠키 토큰 처리 중 오류: {}", e.getMessage());
                        }
                    });
            }
        }

        // 리프레시 토큰 쿠키 만료 처리
        tokenProvider.expireRefreshCookie(response);

        log.info("[JwtLogoutHandler] 로그아웃 처리 완료");
    }
}
