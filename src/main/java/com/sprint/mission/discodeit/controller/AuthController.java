package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
//        LoginResponse loginResponse = authService.login(loginRequest);
//        return ResponseEntity.ok(loginResponse);
//    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();
        log.info("Csrf token 요청: {}", tokenValue);
        log.info("파라미터 이름: {}",csrfToken.getParameterName());
        log.info("헤더 이름: {}",csrfToken.getHeaderName());
        log.info("토큰 값: {}",csrfToken.getToken());

        return ResponseEntity.noContent().build();
    }
}
