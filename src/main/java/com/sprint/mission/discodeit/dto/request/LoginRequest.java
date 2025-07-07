package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "사용자 이름", example = "john_doe")
        @NotBlank(message = "아이디는 필수 입니다.")
        String username,

        @Schema(description = "비밀번호", example = "password123")
        @NotBlank(message = "비밀번호는 필수 입니다.")
        String password
) {
}
