package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping(path = "/login")
    @Override
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: username={}", loginRequest.username());

        UserDto user = authService.login(loginRequest);

        log.debug("로그인 응답: {}", user);

        return ResponseEntity.ok(user);
    }

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
}
