package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "로그인 성공"),
          @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"
              , content = @Content(examples = {
              @ExampleObject(value = "User with {username} not found")})),
          @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음"
              , content = @Content(examples = {
              @ExampleObject(value = "Wrong password")}))
      }
  )
  @PostMapping(path = "/login")
  public ResponseEntity<UserResponseDTO> login(@RequestBody LoginDTO loginDTO) {
    UserResponseDTO loggedInUser = authService.login(loginDTO);

    return ResponseEntity.status(HttpStatus.OK).body(loggedInUser);
  }
}
