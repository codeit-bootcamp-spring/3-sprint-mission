package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : AuthController author
 * : doungukkim date           : 2025. 5. 10. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 5. 10.        doungukkim
 * 최초 생성
 */

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.info("Csrf token 요청: {}", tokenValue);
        log.info("파라미터 이름: {}",csrfToken.getParameterName());
        log.info("헤더 이름: {}",csrfToken.getHeaderName());
        log.info("토큰 값: {}",csrfToken.getToken());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("[AuthController] 세션 기반 사용자 정보 조회 요청(me) 들어옴.");

        if(userDetails == null) {
            log.warn("[AuthController] 유저 인증 실패");
            throw new UserNotFoundException();
        }

        UserResponse response = authService.getCurrentUserInfo(userDetails);

        if (response == null) {
            log.info("[AuthController] AuthService에서 사용자 정보를 가져올 수 없음");
            throw new UserNotFoundException();
        }

        log.info("[AuthController] 사용자 정보 조회 완료: " + response);

        return ResponseEntity.ok(response);
    }
}
