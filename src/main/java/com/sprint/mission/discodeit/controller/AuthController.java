package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

  private final AuthService authService;
  private final UserStatusService userStatusService;

  @PostMapping("/login")
  @Operation(
      summary = "사용자 로그인",
      description = "이메일과 비밀번호를 이용하여 로그인을 수행합니다"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 혹은 로그인 실패",
          content = @Content(schema = @Schema(example = "로그인 실패"))
      )
  })
  public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest) {
    try {

      User user = authService.login(loginRequest);

      boolean isOnline = false;
      try {
        // 활동 상태( 유동적 )
        UserStatus userStatus = userStatusService.find(user.getUserId());
        isOnline = userStatus.isOnline();
      } catch (NoSuchElementException e) {
        // 상태정보가 없으면 기본값( false ) 유지
      }

      // 로그인 성공 시
      UserDTO userDTO = new UserDTO(
          user.getUserId(),
          user.getCreatedAt(),
          user.getUpdatedAt(),
          user.getUserName(),
          user.getEmail(),
          user.getPhoneNumber(),
          user.getStatusMessage(),
          user.getProfileId(),
          isOnline
      );
      return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }
}
