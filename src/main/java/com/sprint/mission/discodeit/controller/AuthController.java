package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CodeMessageResponseDto;
import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

import java.util.NoSuchElementException;

@Tag(
        name = "Auth",
        description = "Auth API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(
          summary = "로그인",
          operationId = "login"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "로그인 성공",
                  content = @Content(
                          mediaType = "*/*",
                          schema = @Schema(implementation = User.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "사용자를 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "User with username {username} not found")
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "비밀번호가 일치하지 않음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "Wrong password")
                  )
          )
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
      try {
          User user = authService.login(loginRequest);
          return ResponseEntity
                  .status(HttpStatus.OK)
                  .body(user);
      } catch (IllegalArgumentException e) {
          return ResponseEntity
                  .status(HttpStatus.BAD_REQUEST)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.PASSWORD_VALID,
                          ResponseMessage.PASSWORD_VALID
                  ));
      } catch (NoSuchElementException e) {
          return ResponseEntity
                  .status(HttpStatus.NOT_FOUND)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.USER_NOT_FOUND,
                          ResponseMessage.USER_NOT_FOUND
                  ));
      }

  }
}
