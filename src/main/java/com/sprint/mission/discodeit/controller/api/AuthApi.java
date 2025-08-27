package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "CSRF 토큰 요청")
    @ApiResponse(
        responseCode = "204",
        description = "CSRF 토큰 발급 성공"
    )
    ResponseEntity<Void> getCsrfToken(
        @Parameter(description = "Spring Security가 자동 주입하는 CSRF 토큰") CsrfToken csrfToken
    );
}