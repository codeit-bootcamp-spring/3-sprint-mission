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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping(path = "login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: username={}", loginRequest.username());

        UserDto user = authService.login(loginRequest);

        log.info("로그인 성공: userId={}", user.id());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(user);
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        System.out.println("[AuthController] ========== CSRF 토큰 발급 요청 시작 ==========");
        System.out.println("[AuthController] 파라미터 이름: " + csrfToken.getParameterName());
        System.out.println("[AuthController] 헤더 이름: " + csrfToken.getHeaderName());
        System.out.println("[AuthController] 토큰 값: " + csrfToken.getToken());
        System.out.println("[AuthController] ========== CSRF 토큰 발급 완료 ==========");

        return ResponseEntity.status(203).build();  // 클라이언트가 받아서 헤더로 보내도록 유도
    }
}