package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * CSRF 토큰 발급 API
     * <p>
     * 프론트엔드에게 CSRF 토큰을 제공하는 엔드포인트
     * Spring Security가 자동으로 CsrfToken 객체를 주입
     *
     * @param csrfToken Spring Security가 자동 주입하는 CSRF 토큰
     * @return CSRF 토큰 정보
     */
    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
        HttpServletResponse response) {
        log.info("토큰 리프레시 요청");
        JwtInformation jwtInformation = authService.refreshToken(refreshToken);
        Cookie refreshCookie = jwtTokenProvider.genereateRefreshTokenCookie(
            jwtInformation.getRefreshToken());
        response.addCookie(refreshCookie);

        JwtDto body = new JwtDto(
            jwtInformation.getUserDto(),
            jwtInformation.getAccessToken()
        );

        return ResponseEntity.ok(body);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> role(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        UserDto userDto = authService.updateRole(request);
        return ResponseEntity.ok(userDto);
    }
}
