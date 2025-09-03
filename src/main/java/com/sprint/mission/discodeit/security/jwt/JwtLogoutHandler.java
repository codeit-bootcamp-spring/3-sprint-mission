package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
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

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        log.debug("JWT 로그아웃 처리 시작");

        try {
            //  쿠키에서 리프레시 토큰을 통해 사용자 정보 무효화
            if (request.getCookies() != null) {
                Arrays.stream(request.getCookies())
                    .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                    .findFirst()
                    .ifPresent(cookie -> {
                        try {
                            String refreshToken = cookie.getValue();
                            UUID userId = jwtTokenProvider.extractUserId(refreshToken);
                            jwtRegistry.invalidateJwtInformationByUserId(userId);
                            log.info("쿠키 리프레시 토큰을 통한 사용자 JWT 정보 무효화 완료: userId={}", userId);
                        } catch (Exception e) {
                            log.warn("쿠키 리프레시 토큰을 통한 사용자 정보 추출 실패: {}", e.getMessage());
                        }
                    });
            }

            // 2JwtRegistry에서 사용자의 JWT 정보 무효화
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
                jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().id());
                log.info("사용자 JWT 정보 무효화 완료: {}", userDetails.getUsername());
            }

            // Refresh 토큰 쿠키 삭제
            deleteRefreshTokenCookie(response, request);

            if (authentication != null && authentication.getName() != null) {
                log.info("사용자 로그아웃 완료: {}", authentication.getName());
            } else {
                log.info("익명 사용자 로그아웃 완료");
            }

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
        }
    }

    /**
     * Refresh 토큰 쿠키를 삭제합니다.
     * MaxAge를 0으로 설정하여 브라우저에서 즉시 삭제되도록 합니다.
     */
    private void deleteRefreshTokenCookie(HttpServletResponse response, HttpServletRequest request) {
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(request.isSecure());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료 설정

        response.addCookie(refreshTokenCookie);

        log.debug("Refresh 토큰 쿠키 삭제 완료");
    }
}
