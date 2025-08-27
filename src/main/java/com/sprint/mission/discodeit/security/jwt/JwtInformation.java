package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtInformation {

  private UserResponse userDto;
  private String accessToken;
  private String refreshToken;

  public void rotate(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }


  public boolean isAccessTokenExpired(
      com.sprint.mission.discodeit.security.jwt.JwtTokenProvider tokenProvider) {
    return !tokenProvider.validateAccessToken(accessToken);
  }

  public boolean isRefreshTokenExpired(
      com.sprint.mission.discodeit.security.jwt.JwtTokenProvider tokenProvider) {
    return !tokenProvider.validateRefreshToken(refreshToken);
  }

  public boolean isAccessTokenValid(
      com.sprint.mission.discodeit.security.jwt.JwtTokenProvider tokenProvider) {
    return tokenProvider.validateAccessToken(accessToken);
  }

  public boolean isRefreshTokenValid(
      com.sprint.mission.discodeit.security.jwt.JwtTokenProvider tokenProvider) {
    return tokenProvider.validateRefreshToken(refreshToken);
  }
}