package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.UpdateUserRoleCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final UserService userService;

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> me(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    return ResponseEntity.ok(userDetails.getUser());
  }

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserResponse> updateRole(@RequestBody UserRoleUpdateRequest request) {
    UpdateUserRoleCommand command = new UpdateUserRoleCommand(request.userId(), request.newRole());
    return ResponseEntity.ok(userService.updateRole(command));
  }
}
