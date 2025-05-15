package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //로그인
    @RequestMapping(path = "/login", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User loginedUser = authService.login(loginRequest);
        return ResponseEntity.ok().body(loginedUser);
    }
}
