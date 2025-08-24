package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(summary = "CSRF 토큰 요청")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "CSRF 토큰 요청 성공"),
        @ApiResponse(responseCode = "400", description = "CSRF 토큰 요청 실패")
    })
    ResponseEntity<Void> getCsrfToken(
        @Parameter(hidden = true) CsrfToken csrfToken
    );

    @Operation(summary = "사용자 권한 수정")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "권한 변경 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))
        )
    })
    ResponseEntity<UserDto> updateRole(
        @Parameter(description = "권한 수정 요청 정보") RoleUpdateRequest request);

    @Operation(summary = "리프레시 토큰을 통한 액세스 토큰 재발급")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "토큰 재발급 성공",
            content = @Content(schema = @Schema(implementation = JwtDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "리프레시 토큰 유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<?> refreshAccessToken(
        @Parameter(description = "쿠키에 담긴 REFRESH_TOKEN", required = true)
        @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
        HttpServletResponse response
    );

} 