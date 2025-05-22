package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(
    @Schema(description = "유저 이름", example = "조현아")
    String username,

    @Schema(description = "유저 이메일", example = "hyeona@example.com")
    String email,

    @Schema(description = "유저 비밀번호", example = "password123")
    String password
) {

}
