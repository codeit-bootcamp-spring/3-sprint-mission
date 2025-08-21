package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.exception.InvalidInputException;
import com.sprint.mission.discodeit.exception.auth.TokenGenerationException;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  public JwtInformation refreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      log.error("Refresh token is missing or blank");
      throw new InvalidInputException("리프레시 토큰이 누락되었거나 잘못된 형식입니다.");
    }
    if (!tokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.error("Invalid or expired refresh token: {}", refreshToken);
      throw new TokenGenerationException("리프레시 토큰이 유효하지 않습니다.");
    }

    String username = tokenProvider.getUsernameFromToken(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)) {
      throw new TokenGenerationException("유효하지 않은 사용자 정보입니다.");
    }

    try {
      String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
      String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);
      log.debug("Access token refreshed for user: {}", username);

      JwtInformation newJwtInformation = new JwtInformation(
          discodeitUserDetails.getUser(),
          newAccessToken,
          newRefreshToken
      );
      jwtRegistry.rotateJwtInformation(
          refreshToken,
          newJwtInformation
      );

      return newJwtInformation;

    } catch (JOSEException e) {
      log.error("Failed to generate new tokens for user: {}", username, e);
      throw new TokenGenerationException("토큰 생성 중 오류가 발생했습니다.");
    }
  }
}
