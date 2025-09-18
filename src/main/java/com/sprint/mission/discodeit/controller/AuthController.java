package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
        @AuthenticationPrincipal UserDetails userDetails
    ) {

        log.debug("[AuthController] 세션 기반 사용자 정보 조회 요청(me) 들어옴.");

        if (userDetails == null) {
            log.debug("[AuthController] 비인증 사용자 (인증 정보 null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserDto userResponse = authService.getCurrentUserInfo(userDetails);

        if (userResponse == null) {
            log.debug("[AuthController] AuthService 에서 사용자 정보를 가져올 수 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        log.debug("[AuthController] 사용자 정보 조회 완료: {}", userResponse);

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(
        @Valid @RequestBody UserRoleUpdateRequest userRoleUpdateRequest
    ) {
        log.debug("[AuthController] 사용자 권한 변경 요청");
        log.debug("[AuthController] 요청 데이터: {}", userRoleUpdateRequest);

        try {
            UserDto userResponse = authService.updateUserRole(userRoleUpdateRequest);
            log.debug("[AuthController] 권한 변경 성공: {}", userResponse);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            log.debug("[AuthController] 권한 변경 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
