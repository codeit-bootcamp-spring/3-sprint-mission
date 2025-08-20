package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
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
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    /**
     * CSRF 토큰 발급 API
     * <p>
     * 프론트엔드에서 CSRF 토큰을 받아올 수 있도록 제공하는 엔드포인트.
     * Spring Security가 자동으로 CsrfToken 객체를 주입해준다.
     *
     * @param csrfToken Spring Security가 자동 주입하는 CSRF 토큰
     * @return CSRF 토큰 정보
     */
    @Override
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 로그인된 사용자 정보 조회 API
     * <p>
     * 세션을 활용한 현재 사용자 정보 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보 DTO
     */
    @Override
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.error("인증된 사용자가 아닙니다. (인증 정보 null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserDto userDto = authService.getCurrentUserInfo(userDetails);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole (@Valid @RequestBody RoleUpdateRequest roleUpdateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.updateUserRole(roleUpdateRequest);

        return ResponseEntity.ok(userDto);
    }
}