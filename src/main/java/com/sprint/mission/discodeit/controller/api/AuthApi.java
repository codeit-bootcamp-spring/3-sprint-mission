package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "203",
          description = "CSRF 토큰 발급 성공"
      )
  })
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "리프레시 토큰으로 access 토큰 재발급")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "토큰 재발급 성공",
          content = @io.swagger.v3.oas.annotations.media.Content(
              mediaType = "application/json",
              schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = JwtDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "리프레시 토큰이 유효하지 않음",
          content = @io.swagger.v3.oas.annotations.media.Content(
              mediaType = "application/json"
          )
      )
  })
  ResponseEntity<JwtDto> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken,
      HttpServletResponse response);

  @Operation(summary = "사용자 권한 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "권한 수정 성공"
      )
  })
  ResponseEntity<UserResponse> updateRole(UserRoleUpdateRequest request);
}
