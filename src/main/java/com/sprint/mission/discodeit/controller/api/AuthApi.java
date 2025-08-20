package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    /* 로그인 기능은 filter 로 옮김 */
//    @Operation(summary = "로그인")
//    @ApiResponses(value = {
//        @ApiResponse(
//            responseCode = "200", description = "로그인 성공",
//            content = @Content(schema = @Schema(implementation = UserDto.class))
//        ),
//        @ApiResponse(
//            responseCode = "404", description = "사용자를 찾을 수 없음",
//            content = @Content(examples = @ExampleObject(value = "User with username {username} not found"))
//        ),
//        @ApiResponse(
//            responseCode = "400", description = "비밀번호가 일치하지 않음",
//            content = @Content(examples = @ExampleObject(value = "Wrong password"))
//        )
//    })
//    ResponseEntity<UserDto> login(
//        @Parameter(description = "로그인 정보") @Valid LoginRequest loginRequest
//    );

    /*csrf 토큰 발급*/
    @GetMapping("/csrf-token")
    ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

    /* 세션ID로 user 정보 가져옴 */
    @GetMapping("/me")
    ResponseEntity<UserDto> getMyInfoFromSession(
        @AuthenticationPrincipal DiscodeitUserDetails userDto);

    /* role 변경 */
    @PutMapping("/role")
    @RolesAllowed("ADMIN")
    ResponseEntity<UserDto> putUserRole(
        @RequestBody UserRoleUpdateRequest userRoleUpdateRequest);
}
