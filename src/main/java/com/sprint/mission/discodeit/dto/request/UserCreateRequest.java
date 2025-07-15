package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 생성 요청 DTO")
public record UserCreateRequest(
        @Schema(description = "사용자 이름", example = "john_doe", minLength = 3, maxLength = 50)
        @NotBlank(message = "사용자 이름은 필수 입니다.")
        @Size(min = 3, max = 50)
        String username,

        @Schema(description = "이메일 주소", example = "john.doe@example.com", format = "email")
        @Email(message = "이메일은 필수 입니다.")
        String email,

        @Schema(description = "비밀번호", example = "password123", minLength = 8)
        @NotBlank(message = "비밀번호는 필수 입니다.")
        @Size(min = 8)
        String password
) {
}
