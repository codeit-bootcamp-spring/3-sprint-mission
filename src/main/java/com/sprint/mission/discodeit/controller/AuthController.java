package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "권한 관리")
public class AuthController {

  private final UserService userService;
  private final AuthService authService;

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음"),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
  })
  @Operation(summary = "유저 로그인", description = "사용자가 로그인합니다.")
  @PostMapping(path = "/login")
  public ResponseEntity<UserDto> login(
      @RequestBody LoginRequest loginRequest
  ) {
    User user = authService.login(loginRequest);
    UserDto userDto = userService.find(user.getId());

    return ResponseEntity.ok(userDto);
  }
}