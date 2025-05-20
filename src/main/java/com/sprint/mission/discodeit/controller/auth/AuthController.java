package com.sprint.mission.discodeit.controller.auth;

import com.sprint.mission.discodeit.entity.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.service.auth.AuthService;
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

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        UserResponseDto user = authService.login(loginRequestDto);
        return ResponseEntity.ok().body(user);
    }
}
