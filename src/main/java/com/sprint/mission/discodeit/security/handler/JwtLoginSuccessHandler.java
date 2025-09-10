package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.exception.auth.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.auth.TokenGenerationException;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      throw new InvalidCredentialsException();
    }

    try {
      String accessToken = tokenProvider.generateAccessToken(userDetails);
      String refreshToken = tokenProvider.generateRefreshToken(userDetails);

      Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
      response.addCookie(refreshCookie);

      JwtDto jwtDto = new JwtDto(
          userDetails.getUser(),
          accessToken
      );

      JwtInformation jwtInformation = new JwtInformation(
          userDetails.getUser(),
          accessToken,
          refreshToken
      );
      jwtRegistry.registerJwtInformation(jwtInformation);
      // 온라인 상태 변경 이벤트 발행
      eventPublisher.publishEvent(
          new UserLogInOutEvent(userDetails.getUser().id(), true));

      var usersCache = cacheManager.getCache("users");
      if (usersCache != null) {
        usersCache.clear();
      }

      response.setStatus(HttpServletResponse.SC_OK);
      objectMapper.writeValue(response.getWriter(), jwtDto);

      log.debug("JWT 토큰이 발급되었습니다. 사용자: {}", userDetails.getUsername());

    } catch (JOSEException e) {
      log.error("JWT 토큰 생성 실패: 사용자 {}", userDetails.getUsername(), e);
      throw new TokenGenerationException("JWT 토큰 생성 중 오류 발생");
    }
  }

}