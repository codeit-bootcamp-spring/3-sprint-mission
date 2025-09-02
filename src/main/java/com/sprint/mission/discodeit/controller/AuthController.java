package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.UpdateUserRoleCommand;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthService authService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    csrfToken.getToken();
    log.debug("CSRF 토큰 요청 처리됨");
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserResponse> updateRole(@RequestBody UserRoleUpdateRequest request) {
    UpdateUserRoleCommand command = new UpdateUserRoleCommand(request.userId(), request.newRole());
    return ResponseEntity.ok(userService.updateRole(command));
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
      HttpServletResponse response) {
    log.info("토큰 리프레시 요청");
    JwtInformation jwtInformation = authService.refreshToken(refreshToken);
    Cookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(
        jwtInformation.getRefreshToken());
    response.addCookie(refreshCookie);

    JwtDto body = new JwtDto(
        jwtInformation.getUserDto(),
        jwtInformation.getAccessToken()
    );
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(body);
  }
}
