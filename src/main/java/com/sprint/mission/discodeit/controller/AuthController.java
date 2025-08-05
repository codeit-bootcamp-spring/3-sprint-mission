package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("[AuthController] 세션 기반 사용자 정보 요청");

        if (userDetails == null) {
            log.warn("[AuthController] 인증된 사용자가 아님!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        log.debug("[AuthController] 인증된 사용자");

        UserResponseDto userResponseDto = authService.getCurrentUser(userDetails);

        log.debug("[AuthController] 사용자 정보 조회 완료: {}", userResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
}
