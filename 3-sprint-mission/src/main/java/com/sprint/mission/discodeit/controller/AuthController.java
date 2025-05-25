package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Tag(
        name = "Auth"
        , description = "인증 API"
)
public class AuthController {
    private final AuthService authService;
    private final UserStatusService userStatusService;

    @Operation(
            summary = "로그인"
            , operationId = "login"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = User.class)))
                    , @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음", content = @Content(schema = @Schema(example = "Wrong password")))
                    , @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(example = "User with username {username} not found")))
            }
    )
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
