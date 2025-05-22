package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// Sprint4 요구사항에는 LoginService가 명시적으로 언급되지 않아 UserService를 임시로 사용합니다.
// 실제 구현 시에는 AuthService 또는 LoginService 등을 사용하는 것이 적절합니다.
// import com.sprint.mission.discodeit.service.UserService;

@RequiredArgsConstructor
@RequestMapping("/api")
@Controller
public class LoginController {

    private final AuthService authService;

    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = authService.login(loginRequest);
            return ResponseEntity.ok("{\"message\": \"Login successful for user: " + user.getUsername() + "\"}");
        } catch (IllegalArgumentException e) {
            // BasicAuthService에서 던지는 예외를 처리.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Login failed: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            // 기타 예기치 않은 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"An unexpected error occurred during login.\"}");
        }
    }
}
