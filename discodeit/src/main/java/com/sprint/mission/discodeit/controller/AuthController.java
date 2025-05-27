package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Tag(name = "인증", description = "인증 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "로그인", description = "로그인을 합니다"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = User.class))),
          @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다", content = @Content(schema = @Schema(hidden = true))),
          @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @PostMapping("/login")
  public ResponseEntity<User> login(@RequestBody LoginRequest request) {
    User login = authService.login(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(login);

  }

}
