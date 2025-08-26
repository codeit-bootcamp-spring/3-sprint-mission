package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final com.sprint.mission.discodeit.security.jwt.JwtTokenProvider tokenProvider;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    Cookie expiredRefreshTokenCookie = tokenProvider.generateExpiredRefreshTokenCookie();
    response.addCookie(expiredRefreshTokenCookie);

    log.debug("JWT logout handler executed - refresh token cookie cleared");
  }
}