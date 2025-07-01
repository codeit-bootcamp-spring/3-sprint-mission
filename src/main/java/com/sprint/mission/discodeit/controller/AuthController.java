package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(mediaType = "*/*")),
        @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "비밀번호가 일치하지 않음"))),
        @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음",
            content = @Content(mediaType = "*/*", examples = @ExampleObject(value = "User not found")))
    })
    @PostMapping(
        value = "/login"
        , consumes = "application/json"        // 클라이언트가 JSON 보내고
        , produces = "application/json"        // 서버도 JSON 응답함
    )
    public ResponseEntity<User> login(
        @Parameter(description = "로그인 정보") @RequestBody LoginRequest loginRequest) {

        User loginUser = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginUser);
    }
}
