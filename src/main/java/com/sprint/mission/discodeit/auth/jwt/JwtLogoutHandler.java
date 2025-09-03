package com.sprint.mission.discodeit.auth.jwt;

import com.sprint.mission.discodeit.auth.jwt.store.JwtSessionRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtSessionRegistry jwtSessionRegistry;

    public JwtLogoutHandler(JwtTokenProvider tokenProvider,
        JwtSessionRegistry jwtSessionRegistry) {
        System.out.println(
            "[JwtLogoutHandler] Constructor called: expired refresh cookie created + session registry injected");
        this.tokenProvider = tokenProvider;
        this.jwtSessionRegistry = jwtSessionRegistry;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        System.out.println(
            "[JwtLogoutHandler] Starting logout process: adding expired refresh cookie to response");

        String authz = request.getHeader("Authorization");
        if (authz != null && authz.startsWith("Bearer ")) {
            String at = authz.substring(7);
            try {
                String atJti = tokenProvider.getTokenId(at);
                System.out.println(
                    "[JwtLogoutHandler] Revoking access token immediately: jti=" + atJti);

                jwtSessionRegistry.revokeByJti(atJti);
            } catch (Exception ignored) {
            }
        }

        if (request.getCookies() != null) {
            Optional<Cookie> rtCookie = Arrays.stream(request.getCookies())
                .filter(c -> JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .findFirst();
            rtCookie.ifPresent(c -> {
                try {
                    String rtJti = tokenProvider.getTokenId(c.getValue());
                    System.out.println(
                        "[JwtLogoutHandler] Revoking refresh token immediately: jti=" + rtJti);
                    jwtSessionRegistry.revokeByJti(rtJti);
                } catch (Exception ignored) {
                }
            });
        }

        tokenProvider.expireRefreshCookie(response);

        System.out.println("[JwtLogoutHandler] Logout completed: expired cookie sent to client");
    }
}
