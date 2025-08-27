package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PostMapping("refresh")
  public ResponseEntity<?> refresh(
      @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response
  ) {
    log.info("토큰 리프레시 요청");

    if (refreshToken == null
        || !jwtTokenProvider.validateRefreshToken(refreshToken)
        || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      log.warn("유효하지 않은 리프레시 토큰(쿠키 누락/서명 불일치/레지스트리 미등록/만료)");
      ErrorResponse body = new ErrorResponse(
          new RuntimeException("유효하지 않은 리프레시 토큰입니다."),
          HttpStatus.UNAUTHORIZED.value()
      );
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    try {
      String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
      DiscodeitUserDetails userDetails =
          (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

      String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

      // 레지스트리 회전(rotation)
      JwtInformation newInfo = new JwtInformation(
          userDetails.getUserDto().id(),
          newAccessToken,
          newRefreshToken,
          jwtTokenProvider.getExpiration(newAccessToken),
          jwtTokenProvider.getExpiration(newRefreshToken),
          "ROLE_" + userDetails.getUserDto().role().name()
      );
      boolean rotated = jwtRegistry.rotateJwtInformation(refreshToken, newInfo);
      if (!rotated) {
        log.warn("리프레시 토큰 회전 실패(이미 무효화되었거나 사용자 불일치)");
        ErrorResponse body = new ErrorResponse(
            new RuntimeException("유효하지 않은 리프레시 토큰입니다."),
            HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
      }

      // 새 리프레시 쿠키 교체
      var refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(newRefreshToken);
      response.addCookie(refreshCookie);

      // 응답: 새 accessToken 본문
      JwtDto body = new JwtDto(userDetails.getUserDto(), newAccessToken);
      return ResponseEntity.ok(body);

    } catch (Exception e) {
      log.error("토큰 재발급 중 오류: {}", e.getMessage(), e);
      ErrorResponse body = new ErrorResponse(
          new RuntimeException("토큰 재발급 중 오류가 발생했습니다."),
          HttpStatus.INTERNAL_SERVER_ERROR.value()
      );
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
  }

  @PutMapping("role")
  public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
