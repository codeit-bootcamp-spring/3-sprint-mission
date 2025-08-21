package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails; // <- 직접 만든 UserDetails
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * CSR용 CSRF 토큰 발급 API
     * - 호출 시 XSRF-TOKEN 쿠키가 내려감
     * - 프론트는 이후 요청의 헤더 X-XSRF-TOKEN에 이 값을 넣어 보냄
     */
    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청: {}", csrfToken != null ? csrfToken.getToken() : "null");
        return ResponseEntity.status(203).build();
    }
    /**
     * 현재 사용자 조회
     * - 로그인(세션 생성) 상태면 UserDto 반환
     * - 아니면 401
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal DiscodeitUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        UserDto user = principal.getUserDto();
        return ResponseEntity.ok(user);
    }
}