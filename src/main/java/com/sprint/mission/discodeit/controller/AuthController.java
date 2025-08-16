package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("me")
    public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.info("세션 기반 사용자 정보 조회 요청");
        UUID userId = userDetails.getUserDto().id();
        UserDto userDto = userService.find(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("사용자 권한 수정 요청");
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }
}