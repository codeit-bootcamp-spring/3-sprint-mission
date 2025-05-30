package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : AuthController author
 * : doungukkim date           : 2025. 5. 10. description    :
 * =========================================================== DATE              AUTHOR
 * NOTE ----------------------------------------------------------- 2025. 5. 10.        doungukkim
 * 최초 생성
 */
@Tag(name = "Auth 컨트롤러", description = "스프린트 미션5 유저 컨트롤러 엔트포인트들 입니다.")
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "사용자 로그인", description = "사용자 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.status(200).body(loginResponse);
    }

}
