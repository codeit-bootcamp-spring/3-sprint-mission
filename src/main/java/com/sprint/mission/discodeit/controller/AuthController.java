package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.store.JwtDto;
import com.sprint.mission.discodeit.security.jwt.store.JwtInformation;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>JWT 기반 인증 시스템에서 토큰 갱신, 권한 변경, CSRF 토큰 제공 등의 
 * 기능을 담당합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>CSRF 토큰 제공</li>
 *   <li>사용자 권한 변경</li>
 *   <li>JWT 토큰 갱신 (토큰 Rotation)</li>
 * </ul>
 *
 * @author HuInDoL
 * @since 1.0.0
 * @see AuthApi
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

    /**
     * CSRF 토큰을 제공합니다.
     * 
     * @param csrfToken Spring Security에서 제공하는 CSRF 토큰
     * @return CSRF 토큰 정보 (응답 본문은 비어있음)
     */
    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

        String tokenValue = csrfToken.getToken();
        log.debug(CONTROLLER_NAME + "CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
            .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
            .body(null);
    }

    /**
     * 사용자의 권한을 변경합니다.
     * 
     * <p>관리자 권한이 있는 사용자만 호출할 수 있습니다.</p>
     * 
     * @param request 권한 변경 요청 정보
     * @return 변경된 사용자 정보
     */
    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateRole(
            @Valid @RequestBody RoleUpdateRequest request
    ) {

        UserDto userDto = authService.updateRole(request);
        log.debug(CONTROLLER_NAME + "사용자 권한 변경 완료: {}", userDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 JWT 토큰을 발급합니다.
     * 
     * <p>토큰 Rotation을 수행하여 보안을 강화합니다:</p>
     * <ol>
     *   <li>기존 토큰 무효화</li>
     *   <li>새로운 Access Token과 Refresh Token 생성</li>
     *   <li>토큰 정보 레지스트리 업데이트</li>
     * </ol>
     * 
     * @param refreshToken 쿠키에서 추출한 리프레시 토큰
     * @param response HTTP 응답 객체
     * @return 새로운 JWT 토큰 정보
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> reIssueAccessByRefreshToken(
            @CookieValue(
                    name = JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
                    required = true
            )
            String refreshToken,
            HttpServletResponse response
    ) {
        log.info(CONTROLLER_NAME + "Refresh Token 재발급 요청");
        JwtInformation jwtInformation = authService.reIssueAccessByRefreshToken(response, refreshToken);

        UserDto userDto = jwtInformation.userDto();
        String newAccessToken = jwtInformation.accessToken();
        JwtDto jwtDto = new JwtDto(userDto, newAccessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jwtDto);
    }
}
