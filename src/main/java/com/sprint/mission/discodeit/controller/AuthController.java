package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        log.debug("[AuthController] ========== CSRF 토큰 발급 요청 시작 ==========");
        log.debug("[AuthController] 파라미터 이름: " + csrfToken.getParameterName());
        log.debug("[AuthController] 헤더 이름: " + csrfToken.getHeaderName());
        log.debug("[AuthController] 토큰 값: " + csrfToken.getToken());
        log.debug("[AuthController] ========== CSRF 토큰 발급 완료 ==========");

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(
        @CookieValue(
            name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            required = false
        )
        String refreshToken,
        HttpServletResponse response) {

        log.info("[AuthController] 토큰 재발급 요청 시작");
        log.info("[AuthController] 쿠키에서 추출한 리프레시 토큰: {}", refreshToken != null ? "있음" : "없음");

        if (refreshToken == null) {
            log.warn("[AuthController] 리프레시 토큰이 쿠키에 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.warn("[AuthController] 리프레시 토큰 유효성 검사 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // JwtRegistry에서 리프레시 토큰 활성 상태 확인
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            log.warn("[AuthController] 비활성 리프레시 토큰으로 재발급 시도");
            jwtTokenProvider.expireRefreshCookie(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserById(
            userId);

        try {
            // 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            // 토큰 회전: 기존 리프레시 토큰을 새 토큰으로 교체
            UserDto userDto = userDetails.getUserDto();
            JwtInformation newJwtInformation = new JwtInformation(userDto, newAccessToken,
                newRefreshToken);
            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            // 새 리프레시 토큰 쿠키 설정
            response.addCookie(jwtTokenProvider.generateRefreshTokenCookie(newRefreshToken));

            JwtDto body = new JwtDto(userDto, newAccessToken);

            log.info("[AuthController] 토큰 재발급 완료: userId={}", userId);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("[AuthController] 토큰 재발급 중 오류: {}", e.getMessage(), e);
            jwtTokenProvider.expireRefreshCookie(response);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateRole(
        @RequestBody RoleUpdateRequest roleUpdateRequest) {

        log.info("[AuthController] 사용자 권한 변경 요청");
        log.info("[AuthController] 요청 데이터: userId={}, newRole={}",
            roleUpdateRequest.userId(), roleUpdateRequest.newRole());

        // 권한 변경 실행
        UserDto updatedUser = userService.updateUserRole(roleUpdateRequest);

        // 권한이 변경된 사용자의 모든 JWT 토큰 무효화 (강제 로그아웃)
        log.info("[AuthController] 권한 변경된 사용자({})의 모든 토큰 무효화 시작", updatedUser.id());
        jwtRegistry.invalidateJwtInformationByUserId(updatedUser.id());
        log.info("[AuthController] 권한 변경된 사용자의 모든 토큰 무효화 완료");

        return ResponseEntity.ok(updatedUser);
    }
}