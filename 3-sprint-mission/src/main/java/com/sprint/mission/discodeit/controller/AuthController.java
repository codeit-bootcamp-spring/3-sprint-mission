package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthAPI;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthAPI {
    private final AuthService authService;
    private final UserStatusService userStatusService;

    @PostMapping(
            value = "/login"
    )
    public ResponseEntity<User> login(
            @RequestBody LoginRequest loginDTO
    ) {
        User user = authService.login(loginDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
