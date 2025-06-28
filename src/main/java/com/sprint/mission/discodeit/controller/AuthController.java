package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.User.LoginRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
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

    /**
     * 사용자 로그인 인증
     *
     * @param loginRequest 유저 로그인 요청 DTO
     * @return 로그인된 User(HTTP 200 OK)
     */
    @PostMapping(path = "/login")
    @Override
    public ResponseEntity<UserDto> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        UserDto authUser = authService.login(loginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authUser);
    }
}
