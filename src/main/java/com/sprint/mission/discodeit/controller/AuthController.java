package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.RoleUpdateRequest;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {

        String tokenValue = csrfToken.getToken();
        log.debug(CONTROLLER_NAME + "CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity
            .status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
            .body(null);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {

        log.debug(CONTROLLER_NAME + "세션 기반 사용자 정보 조회 요청(me) 들어옴");

        if (userDetails == null) {
            log.debug(CONTROLLER_NAME + "인증된 사용자가 아님! (인증 정보 null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug(CONTROLLER_NAME + "인증된 사용자 확인");

        UserDto userDto = authService.getCurrentUserInfo(userDetails);

        log.debug(CONTROLLER_NAME + "사용자 정보 조회 완료: {}", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(userDto);
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
}
