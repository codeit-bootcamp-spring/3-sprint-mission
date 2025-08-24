package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

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

    @Operation(summary = "세션 정보를 활용한 현재 사용자 정보 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "올바르지 않은 세션")
    })
    ResponseEntity<UserDto> me(@Parameter(hidden = true) DiscodeitUserDetails userDetails);
} 