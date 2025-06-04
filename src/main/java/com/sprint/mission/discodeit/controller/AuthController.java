package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", operationId = "login")
    @ApiResponses({
            @ApiResponse(responseCode = "200",  description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest req) {
        User user = authService.login(req.username(), req.password());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}