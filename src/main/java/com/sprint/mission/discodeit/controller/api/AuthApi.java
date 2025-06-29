package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth")
public interface AuthApi {

    @Operation(summary = "로그인")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(examples = @ExampleObject(value = "로그인"))),
        @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
            content = @Content(examples = @ExampleObject(value = "비밀번호가 일치하지 않음"))),
        @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "User not found")))
    })
    ResponseEntity<UserDto> login(
        @Parameter(description = "로그인 정보") LoginRequest loginRequest);
}

