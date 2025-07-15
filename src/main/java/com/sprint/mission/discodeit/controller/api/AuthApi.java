package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.authService.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : AuthControllerApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */
@Tag(name = "Auth 컨트롤러", description = "스프린트 미션5 유저 컨트롤러 엔트포인트들 입니다.")
@RequestMapping("api/auth")
public interface AuthApi {

    @Operation(summary = "사용자 로그인", description = "사용자 로그인을 합니다.")
    @PostMapping("/login")
    ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest);
}