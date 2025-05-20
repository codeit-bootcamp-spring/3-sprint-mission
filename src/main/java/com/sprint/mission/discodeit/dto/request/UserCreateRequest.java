package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 생성 요청 DTO")
public record UserCreateRequest(
        @Schema(description = "사용자 이름", example = "john_doe", minLength = 3, maxLength = 50)
        String username,

        @Schema(description = "이메일 주소", example = "john.doe@example.com", format = "email")
        String email,

        @Schema(description = "비밀번호", example = "password123", minLength = 8)
        String password
) {}
