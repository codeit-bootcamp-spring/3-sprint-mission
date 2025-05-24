package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Controller
@ResponseBody
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
}
