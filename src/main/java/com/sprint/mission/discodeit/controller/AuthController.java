package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping(
        value = "/login"
//        , consumes = "application/json"        // 클라이언트가 JSON 보내고
//        , produces = "application/json"        // 서버도 JSON 응답함
    )
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserDto loginUser = authService.login(loginRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginUser);
    }
}
