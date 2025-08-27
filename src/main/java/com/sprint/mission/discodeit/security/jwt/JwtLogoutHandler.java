package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        log.debug("JWT 로그아웃 처리 시작");

        try {
            // JwtRegistry에서 사용자의 JWT 정보 무료화
            if (authentication != null && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
                jwtRegistry.invalidateJwtInformationByUserId(userDetails.getUserDto().id());
                log.info("사용자 JWT 정보 무효화 완료 : {}", userDetails.getUsername());
            }

            // Refreshdd 토큰 쿠키 삭제
            deleteRefreshTokenCookie(response, request);

            // 로그 출력 - 인증된 사용자 정보가 있을 경우
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
