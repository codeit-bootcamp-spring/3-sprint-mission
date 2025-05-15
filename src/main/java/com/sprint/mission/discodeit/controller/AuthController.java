package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<AuthLoginReponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}