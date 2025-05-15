package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /* 유저 로그인 */
    @RequestMapping(value = "/login"
            , method = RequestMethod.POST)
    public ResponseEntity<User> login(
            @RequestBody LoginRequest loginRequest
    ) {
        User user = this.authService.login(loginRequest);

        return ResponseEntity.ok().body(user);
    }

}
