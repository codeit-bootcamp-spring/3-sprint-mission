package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * JWT 로그아웃 처리 핸들러.
 *
 * <ul>
 *   <li>쿠키에 저장된 리프레시 토큰 삭제</li>
 *   <li>클라이언트 로그아웃 시 토큰 무효화 지원</li>
 *   <li>JS 접근 제한 및 HTTPS 환경 권장</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final String refreshTokenCookieName;

    public JwtLogoutHandler(@Value("${jwt.refresh-token-cookie-name}") String refreshTokenCookieName) {
        this.refreshTokenCookieName = refreshTokenCookieName;
    }

    /**
     * 로그아웃 처리
     *
     * <p>요청 쿠키에서 리프레시 토큰을 찾아 삭제 처리</p>
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 객체 (사용자 정보, 없을 수 있음)
     */
    @Override
    public void logout(HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) {

        // 쿠키 존재 여부 확인
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // 리프레시 토큰 쿠키인지 검증
                if (refreshTokenCookieName.equals(cookie.getName())) {

                    // 기존 쿠키 값 삭제, 만료 처리
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    response.addCookie(cookie);

                    log.info("리프레시 토큰 쿠키 삭제 완료: {}", refreshTokenCookieName);
                }
            }
        }
    }
}