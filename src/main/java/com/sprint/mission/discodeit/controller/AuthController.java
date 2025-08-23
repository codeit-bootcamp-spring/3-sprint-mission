package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping(path = "login")
  public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
    log.info("로그인 요청: username={}", loginRequest.username());

    UserDto user = authService.login(loginRequest);

    log.debug("로그인 응답: {}", user);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  /**
   * CSRF 토큰을 요청하기 위한 엔드포인트.
   *
   * <p>요청 시 Spring Security가 CSRF 토큰을 쿠키에 담아 내려보낸다.
   * 응답 본문은 없으며 상태 코드 204를 반환한다.</p>
   *
   * @param csrfToken 요청에 매핑된 CSRF 토큰 (Spring이 자동 주입)
   * @return 204 No Content
   */
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    // CSRF 토큰 발급 요청 로그
    log.debug("CSRF 토큰 요청");
    log.trace("CSRF 토큰: {}", csrfToken.getToken());

    // 응답 본문 없이 204 반환
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
