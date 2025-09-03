package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieUtil {

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpiration / 1000));
        return cookie;
    }

    public Cookie createExpiredCookie() {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
