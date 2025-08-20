package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;

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

    @Operation(
        summary = "현재 사용자 정보 조회",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 요청"
        )
    })
    @GetMapping("/me")
    ResponseEntity<UserDto> getCurrentUser(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );
}