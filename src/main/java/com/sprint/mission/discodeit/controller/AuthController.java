package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  //로그인
  @PostMapping(value = "/auths")
  public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
    User loginedUser = authService.login(loginRequest);
    return ResponseEntity.ok().body(loginedUser);
  }
}
