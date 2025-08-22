package com.sprint.mission.discodeit.controller;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtDto;
import com.sprint.mission.discodeit.security.jwt.store.JwtSessionRegistry;
import com.sprint.mission.discodeit.security.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>로그인 기능을 제공하며, 클라이언트로부터 로그인 요청을 받아
 * {@link AuthService}를 통해 인증 로직을 수행합니다.</p>
 *
 * <p>요청에 대한 로깅을 수행하며, 클라이언트 IP, User-Agent, 처리 시간을 기록합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final HttpServletRequest request;

    private static final String CONTROLLER_NAME = "[AuthController] ";
    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtSessionRegistry jwtSessionRegistry;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

        String tokenValue = csrfToken.getToken();
        log.debug(CONTROLLER_NAME + "CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
            .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
            .body(null);
    }

    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateRole(
            @RequestBody RoleUpdateRequest request
    ) {

        UserDto userDto = authService.updateRole(request);
        log.debug(CONTROLLER_NAME + "사용자 권한 변경 완료: {}", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> reIssueAccessByRefreshToken(
            @CookieValue(
                    name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            )
            String refreshToken,
            HttpServletResponse response) {

        // 유효하지 않은 RefreshToken이면 401 반환
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 유효 쿠키면 쿠키 값 추출(이전 refreshToken 취급)
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String oldRefreshJti = jwtTokenProvider.getTokenId(refreshToken);

        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);

        try {
            // 사용자에게 새 토큰 발급
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            // Rotation: 이전 리프레시 무효화 및 교체
            String newRefreshJti = jwtTokenProvider.getTokenId(newAccessToken);

            // 폐기된 기존 RefreshToken 재사용 차단 (401)
            if (jwtSessionRegistry.isRevoked(oldRefreshJti)) {
                // 쿠키 만료 처리: 클라이언트 보관 RT 제거
                jwtTokenProvider.expireRefreshToken(response);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Rotation
            jwtSessionRegistry.markReplaced(oldRefreshJti, newRefreshJti);

            // 신규 토큰 메타데이터 저장
            JwtTokenEntity accessEntity = jwtTokenProvider.toEntity(newAccessToken);
            JwtTokenEntity refreshEntity = jwtTokenProvider.toEntity(newRefreshToken);
            jwtSessionRegistry.register(accessEntity);
            jwtSessionRegistry.register(refreshEntity);

            // 리프레시 쿠키 교체
            // HTTP 응답 헤더(Set-Cookie)에 리프레시 쿠키 추가
            jwtTokenProvider.addRefreshCookie(response, newRefreshToken);

            UserDto userDto = userDetails.getUserDto();
            JwtDto jwtDto = new JwtDto(userDto, newAccessToken);

            return ResponseEntity.status(HttpStatus.OK).body(jwtDto);
        } catch (JOSEException e) {
            // 리프레시 토큰 재발급 도중 발생한 예외 처리 (500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
