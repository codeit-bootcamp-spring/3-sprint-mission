package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
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
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    // CSRF 토큰을 발급하는 API
    @GetMapping(path = "/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        /*
         * Spring Security는 POST, PUT, DELETE 요청에 대해서만 CSRF 검증을 함
         * 그래서 일반적인 GET / 요청만으로는 CSRF 토큰이 초기화되지 않을 수 있음
         * 따라서 명시적으로 csrfToken.getToken()을 호출해서 강제로 토큰을 초기화하는 트리거를 만들어줌
         */
        log.info("[AuthController] ========== CSRF 토큰 발급 요청 시작 ==========");
        log.info("[AuthController] 파라미터 이름: {}", csrfToken.getParameterName());
        log.info("[AuthController] 헤더 이름: {} ", csrfToken.getHeaderName());
        log.info("[AuthController] 토큰 값: {} ", csrfToken.getToken());
        log.info("[AuthController] ========== CSRF 토큰 발급 완료 ==========");

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserDto> getUser(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ){
        log.info("[AuthController] 세션 기반 사용자 정보 조회 요청(me) 들어옴.");

        if (userDetails == null) {
            // @AuthenticationPrincipal로 주입받은 userDetails가 null이면 인증되지 않은 상태
            log.warn("[AuthController] 인증된 사용자가 아님 (인증 정보 null)");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UUID userId = userDetails.getId();
        UserDto updatedUserDto = userService.find(userId);

        log.info("[AuthController] 사용자 정보 조회 완료");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserDto);
    }

    @PutMapping(path = "/role")
    public ResponseEntity<UserDto> updateUserRole(
        @RequestBody UserRoleUpdateRequest updateRequest
    ){
        log.info("[AuthController] 사용자 권한 수정 요청 들어옴");

        UserDto userDto = authService.updateUserRole(updateRequest);
        log.info("[AuthController] 사용자 권한 수정 완료");

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }
}
