package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.User.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * 사용자 로그인 인증
     *
     * @param loginRequest 유저 로그인 요청 DTO
     * @return 로그인된 User(HTTP 200 OK)
     */
    @Operation(
            summary = "로그인",
            operationId = "login"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음", content = @Content(examples = @ExampleObject("Wrong password"))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject("User with username {username} not found")))
            }
    )
    @PostMapping(path = "/login")
    public ResponseEntity<User> login(
            @RequestBody LoginRequest loginRequest
    ) {
        User authUser = authService.login(loginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authUser);
    }
}
