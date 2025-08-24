package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    /**
     * CSRF 토큰을 요청하기 위한 엔드포인트.
     *
     * <p>요청 시 Spring Security가 CSRF 토큰을 쿠키에 담아 내려보낸다.
     * 응답 본문은 없으며 상태 코드 204를 반환한다.</p>
     *
     * @param csrfToken 요청에 매핑된 CSRF 토큰 (Spring이 자동 주입)
     * @return 204 No Content
     */
    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        // CSRF 토큰 발급 요청 로그
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());

        // 응답 본문 없이 204 반환
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    /**
     * 현재 로그인한 사용자의 정보를 조회하는 엔드포인트.
     *
     * <p>Spring Security의 {@link AuthenticationPrincipal}을 통해
     * 현재 인증된 {@link DiscodeitUserDetails}를 주입받아 사용자 식별자를 얻고,
     * {@link UserService}를 통해 DB에서 상세 정보를 조회하여 반환한다.</p>
     *
     * @param userDetails 인증된 사용자 정보 (SecurityContext에서 자동 주입)
     * @return HTTP 200 OK와 함께 조회된 사용자 DTO 반환
     */
    @GetMapping("me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.info("내 정보 조회 요청");

        UUID userId = userDetails.getUserDto().id();
        UserDto userDto = userService.find(userId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDto);
    }
}
