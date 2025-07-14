package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthAPI;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Slf4j
public class AuthController implements AuthAPI {

  private final AuthService authService;

  @PostMapping(
      path = "login"
  )
  public ResponseEntity<UserDto> login(
      @RequestBody @Valid LoginRequest loginRequest
  ) {
    log.info("로그인 요청 username={}", loginRequest.username());

    UserDto user = authService.login(loginRequest);
    log.info("로그인 성공 username={}, userId={}", user.username(), user.id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
