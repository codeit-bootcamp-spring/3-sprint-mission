package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    /* 로그인 기능은 filter 로 옮김 */
//    /* 유저 로그인 */
//    @PostMapping(path = "/login")
//    @Override
//    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
//        UserDto userDto = authService.login(loginRequest);
//        return ResponseEntity
//            .status(HttpStatus.OK)
//            .body(userDto);
//    }

    /*csrf 토큰 발급*/
    @GetMapping("/csrf-token")
    @Override
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.debug("[CSRF 토큰 발급 요청]");
        log.debug("[CSRF 토큰 발급 완료]: - 헤더 이름: {}, - 토큰 값: {}", csrfToken.getHeaderName(),
            csrfToken.getToken());

        return ResponseEntity.noContent().build();
    }

    /* 세션ID로 user 정보 조회 */
    @GetMapping("/me")
    @Override
    public ResponseEntity<UserDto> getMyInfoFromSession(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.debug("[세션으로 Principal 정보 요청]");

        if (userDetails == null) {
            log.debug("[세션으로 Principal 정보 요청 실패]");
            throw new UserNotFoundException();
        }

        log.debug("[세션으로 Principal 정보 요청 성공] userDto: {}", userDetails);
        return ResponseEntity.ok().body(userDetails.getUserDto());
    }

    /* role 변경 */
    @PutMapping("/role")
    @RolesAllowed("ROLE_ADMIN")
    @Override
    public ResponseEntity<UserDto> putUserRole(
        @RequestBody UserRoleUpdateRequest userRoleUpdateRequest) {
        log.debug("[유저 role 변경 요청]");

        UserDto userDto = authService.updateUserRole(userRoleUpdateRequest);
        log.debug("[유저 role 변경 요청 성공] new role: {}", userDto.getRole());
        return ResponseEntity.ok().body(userDto);

    }
}
