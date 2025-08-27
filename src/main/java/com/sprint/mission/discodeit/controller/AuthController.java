package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserMapper userMapper;

    /**
     * CSRF 토큰 발급 API
     * <p>
     * 프론트엔드에서 CSRF 토큰을 받아올 수 있도록 제공하는 엔드포인트.
     * Spring Security가 자동으로 CsrfToken 객체를 주입해준다.
     *
     * @param csrfToken Spring Security가 자동 주입하는 CSRF 토큰
     * @return CSRF 토큰 정보
     */
    @Override
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.info("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.noContent().build();
    }

    /**
     * 리프레시 토큰으로 액세스 토큰 재발급
     * <p>
     * 리프레시 토큰을 사용해 액세스 토큰을 재발급하는 API.
     * 리프레시 토큰은 쿠키에 저장되어 있으며, 이를 통해 액세스 토큰을 재발급한다.
     *
     * @param refreshToken 리프레시 토큰
     * @param response HTTP 응답 객체
     * @return 재발급된 액세스 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refreshToken(
        @CookieValue(
            name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            required = false
        )
        String refreshToken,
        HttpServletResponse response
    ) {
        log.info("[AuthController] 리프레시 토큰 재발급 요청");

        JwtDto jwtDto = authService.refreshToken(refreshToken, response);

        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("[AuthController] 사용자 권한 수정 요청");
        User user = authService.updateRole(request);
        UserDto userDto = userMapper.toDto(user);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }
}