package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refreshToken(
        @CookieValue(
            name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            required = false
        ) String refreshToken,
        HttpServletResponse response
    ) {

        log.debug("[AuthController] 리프레시 토큰 재발급 요청");

        JwtDto jwtDto = authService.refreshToken(refreshToken, response);

        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
        @Valid @RequestBody UserRoleUpdateRequest userRoleUpdateRequest
    ) {
        log.debug("[AuthController] 사용자 권한 변경 요청");
        log.debug("[AuthController] 요청 데이터: {}", userRoleUpdateRequest);

        try {
            UserDto userResponse = authService.updateUserRole(userRoleUpdateRequest);
            log.debug("[AuthController] 권한 변경 성공: {}", userResponse);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            log.debug("[AuthController] 권한 변경 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
