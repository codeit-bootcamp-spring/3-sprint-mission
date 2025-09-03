package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtDto;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.RefreshTokenCookieUtil;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieUtil cookieUtil;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("사용자 권한 수정 요청");
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken, HttpServletResponse response) {
        log.info("액세스 토큰 재발급 요청");
        JwtInformation jwtInfo = authService.reissueTokens(refreshToken);

        Cookie newRefreshTokenCookie = cookieUtil.createRefreshTokenCookie(jwtInfo.refreshToken());
        response.addCookie(newRefreshTokenCookie);

        JwtDto result = new JwtDto(jwtInfo.userDto(), jwtInfo.accessToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}