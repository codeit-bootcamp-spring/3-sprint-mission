package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.dto.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

	private final AuthService authService;

	@GetMapping("/me")
	public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails principal) {
		return ResponseEntity.ok(principal.getUserDto());
	}

	@PutMapping("/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDto> updateRole(@Valid @RequestBody RoleUpdateRequest request) {
		UserDto updated = authService.updateUserRole(request);
		return ResponseEntity.ok(updated);
	}
}
