package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(
        @CookieValue(
            name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            required = false
        )
        String refreshToken,
        HttpServletResponse response
    ) {
        log.debug("[AuthController] Refresh 토큰 재발급 요청");
        JwtDto jwtDto = authService.refresh(refreshToken, response);

        return ResponseEntity.status(HttpStatus.OK).body(jwtDto);
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponseDto> updateRole(
        @RequestBody RoleUpdateRequest request) {

        log.debug("[AuthController] 사용자 권한 변경 요청");
        log.debug("[AuthController] 변경 요청 정보: {}", request);

        UserResponseDto userResponseDto = userService.updateRole(request);

        log.debug("[AuthController] 사용자 권한 변경 성공: {}", userResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
}
