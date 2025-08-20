package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final UserService userService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        log.debug("[AuthController] ========== CSRF 토큰 발급 요청 시작 ==========");
        log.debug("[AuthController] 파라미터 이름: " + csrfToken.getParameterName());
        log.debug("[AuthController] 헤더 이름: " + csrfToken.getHeaderName());
        log.debug("[AuthController] 토큰 값: " + csrfToken.getToken());
        log.debug("[AuthController] ========== CSRF 토큰 발급 완료 ==========");

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.debug("[AuthController] /me 요청 시작");
        log.debug("[AuthController] userDetails = {}", userDetails);
        log.debug("[AuthController] dto = {}", userDetails != null ? userDetails.getUserDto() : "null");
        
        if (userDetails == null) {
            log.warn("[AuthController] /me 요청 실패 - 인증 정보 null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        log.debug("[AuthController] /me 요청 성공 - 인증된 사용자 username: {}", userDetails.getUsername());

        return ResponseEntity.ok(userDetails.getUserDto());
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateRole(
        @RequestBody RoleUpdateRequest roleUpdateRequest) {

        log.debug("[AuthController] 사용자 권한 변경 요청");
        log.debug("[AuthController] 요청 데이터: " + roleUpdateRequest);

        UserDto updatedUser = userService.updateUserRole(roleUpdateRequest);
        return ResponseEntity.ok(updatedUser);

    }
}