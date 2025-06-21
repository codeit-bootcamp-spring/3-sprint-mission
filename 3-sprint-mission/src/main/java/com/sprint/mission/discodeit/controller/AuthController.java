package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthAPI;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthAPI {

  private final AuthService authService;
  private final UserStatusService userStatusService;

  @PostMapping(
      value = "/login"
  )
  public ResponseEntity<UserDTO> login(
      @RequestBody LoginRequest loginDTO
  ) {
    UserDTO user = authService.login(loginDTO);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
