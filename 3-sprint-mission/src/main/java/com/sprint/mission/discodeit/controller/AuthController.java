package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refreshToken(
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response) {
        log.info("토큰 리프레시 요청");
        JwtInformation jwtInformation = authService.refreshToken(refreshToken);
        Cookie refreshCookie = jwtTokenProvider.generateRefreshTokenCookie(
                jwtInformation.getRefreshToken()
        );
        response.addCookie(refreshCookie);

        JwtDto responseBody = new JwtDto(
                jwtInformation.getUserDto(),
                jwtInformation.getAccessToken()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
            @RequestBody RoleUpdateRequest request) {

        log.info("권한 수정 요청");
        UserDto userDto = authService.updateUserRole(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }
}