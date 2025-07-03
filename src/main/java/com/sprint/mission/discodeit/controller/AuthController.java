package com.sprint.mission.discodeit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.mapper.ResponseMapper;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @PostMapping(path = "login")
  public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    UserResponse userResponse = ResponseMapper.toResponse(user);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userResponse);
  }
}
