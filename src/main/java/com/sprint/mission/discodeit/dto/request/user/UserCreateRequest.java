package com.sprint.mission.discodeit.dto.request.user;

import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserCreateRequest(

    @Schema(description = "유저 이름", example = "옹길동", required = true)
    String username,
    @Schema(description = "유저 이메일", example = "user@example.com", required = true)
    String email,
    @Schema(description = "유저 비밀번호", example = "rlfehd1234", required = true)
    String password,
    UserStatus status
) {

}
