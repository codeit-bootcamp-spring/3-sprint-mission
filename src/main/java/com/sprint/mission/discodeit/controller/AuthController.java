package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * CSRF 토큰을 요청하기 위한 엔드포인트.
     *
     * <p>요청 시 Spring Security가 CSRF 토큰을 쿠키에 담아 내려보낸다.
     * 응답 본문은 없으며 상태 코드 204를 반환한다.</p>
     *
     * @param csrfToken 요청에 매핑된 CSRF 토큰 (Spring이 자동 주입)
     * @return 204 No Content
     */
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        // CSRF 토큰 발급 요청 로그
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());

        // 응답 본문 없이 204 반환
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }

    /**
     * 쿠키에 담긴 리프레시 토큰으로 액세스 토큰을 재발급합니다.
     *
     * @param refreshToken REFRESH_TOKEN 쿠키 값
     * @param response HttpServletResponse, 새 쿠키 설정에 사용
     * @return 새 액세스 토큰과 사용자 정보(JwtDto) 또는
     *         유효하지 않은 토큰 시 ErrorResponse (401 Unauthorized)
     */
    @PostMapping("refresh")
    public ResponseEntity<?> refreshAccessToken(
        @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        // 쿠키에 토큰이 없거나 유효하지 않으면 401 반환
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(new RuntimeException("Invalid refresh token"), HttpServletResponse.SC_UNAUTHORIZED));
        }

        // 사용자 이름(subject) 추출
        String username = jwtTokenProvider.getSubject(refreshToken);

        // UserDto 조회
        UserDto userDto = userService.findByUsername(username);

        // 새 액세스 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(username);

        // 리프레시 토큰 Rotation
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (jwtTokenProvider.getRefreshTokenValidityMs() / 1000));
        response.addCookie(refreshCookie);

        // JwtDto 응답
        JwtDto jwtDto = new JwtDto(userDto, newAccessToken);
        return ResponseEntity.ok(jwtDto);
    }

}
