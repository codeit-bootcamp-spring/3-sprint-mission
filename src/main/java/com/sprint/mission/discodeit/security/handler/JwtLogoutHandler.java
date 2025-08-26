package com.sprint.mission.discodeit.security.handler;

import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    Cookie refreshTokenExpirationCookie = tokenProvider.genereateRefreshTokenExpirationCookie();
    response.addCookie(refreshTokenExpirationCookie);
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME))
          .findFirst()
          .ifPresent(cookie -> {
            try {
              SignedJWT signedJWT = SignedJWT.parse(cookie.getValue());
              String userId = signedJWT.getJWTClaimsSet().getStringClaim("userId");
              jwtRegistry.invalidateJwtInformationByUserId(UUID.fromString(userId));
            } catch (Exception e) {
              log.warn("Failed to invalidate JWT information on logout", e);
            }
          });
    }

    log.debug("JWT 로그아웃 핸들러 실행 - 리프레시 토큰 쿠키 삭제");
  }
}