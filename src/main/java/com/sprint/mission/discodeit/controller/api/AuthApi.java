package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.jwt.JwtDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증/권한 관련 API")
public interface AuthApi {

    @Operation(
        summary = "CSRF 토큰 발급",
        description = """
            XSRF-TOKEN 쿠키를 발급합니다. 응답 바디는 없습니다(204).
            프론트엔드는 이후 요청 헤더에 'X-XSRF-TOKEN'을 포함해야 합니다.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "성공(바디 없음)")
    })
    ResponseEntity<Void> getCsrfToken(
        @Parameter(hidden = true) org.springframework.security.web.csrf.CsrfToken csrfToken
    );

    @Operation(
        summary = "Access/Refresh 재발급",
        description = "Refresh 토큰 쿠키를 사용해 JWT를 재발급합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "재발급 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = JwtDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", description = "Refresh 토큰 누락/유효하지 않음",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    ResponseEntity<JwtDto> refreshToken(
        @Parameter(
            name = "refreshToken",
            in = ParameterIn.COOKIE,
            description = "Refresh 토큰(쿠키). 실제 쿠키명은 JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME 상수로 정의됨."
        ) String refreshToken,
        @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(
        summary = "사용자 권한 변경",
        description = "관리자가 사용자 권한을 변경합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "권한 변경 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(name = "성공 예시",
                    value = """
                        {
                          "id": "11111111-1111-1111-1111-111111111111",
                          "username": "alice",
                          "role": "CHANNEL_MANAGER"
                        }
                        """)
            )
        ),
        @ApiResponse(
            responseCode = "400", description = "요청 본문 유효성 오류",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", description = "대상 사용자를 찾을 수 없음",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "403", description = "권한 없음(관리자만 가능)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        )
    })
    ResponseEntity<UserDto> updateUserRole(
        @RequestBody(
            required = true,
            description = "변경할 사용자/역할 정보",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserRoleUpdateRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "userId": "11111111-1111-1111-1111-111111111111",
                      "newRole": "CHANNEL_MANAGER"
                    }
                    """)
            )
        )
        @org.springframework.web.bind.annotation.RequestBody UserRoleUpdateRequest userRoleUpdateRequest
    );
}
