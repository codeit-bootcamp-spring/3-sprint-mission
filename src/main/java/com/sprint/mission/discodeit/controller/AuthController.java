package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    // CSRF 토큰을 발급하는 API
    @GetMapping(path = "/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        /*
         * Spring Security는 POST, PUT, DELETE 요청에 대해서만 CSRF 검증을 함
         * 그래서 일반적인 GET / 요청만으로는 CSRF 토큰이 초기화되지 않을 수 있음
         * 따라서 명시적으로 csrfToken.getToken()을 호출해서 강제로 토큰을 초기화하는 트리거를 만들어줌
         */
        log.info("[AuthController] ========== CSRF 토큰 발급 요청 시작 ==========");
        log.info("[AuthController] 파라미터 이름: {}", csrfToken.getParameterName());
        log.info("[AuthController] 헤더 이름: {} ", csrfToken.getHeaderName());
        log.info("[AuthController] 토큰 값: {} ", csrfToken.getToken());
        log.info("[AuthController] ========== CSRF 토큰 발급 완료 ==========");

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


     // 리프레시 토큰으로 액세스 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(
        /* 설명. @CookieValue 어노테이션을 사용하면 HTTP 요청 헤더(Cookie)의 쿠키 값을 자동으로 추출해준다. */
        @CookieValue("REFRESH_TOKEN") String refreshToken,
        HttpServletResponse response) {

        log.debug("[AuthController] RefreshToken 으로 AccessToken 재발급 요청");
        JwtDto jwtDto = authService.refreshToken(refreshToken, response);
        log.debug("[AuthController] Refresh 토큰으로 AccessToken 재발급 완료");

        return ResponseEntity.ok(jwtDto);
    }

    @PutMapping(path = "/role")
    public ResponseEntity<UserDto> updateUserRole(
        @Valid @RequestBody UserRoleUpdateRequest updateRequest
    ){
        log.info("[AuthController] 사용자 권한 수정 요청 들어옴");

        UserDto userDto = authService.updateUserRole(updateRequest);
        log.info("[AuthController] 사용자 권한 수정 완료");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }
}
