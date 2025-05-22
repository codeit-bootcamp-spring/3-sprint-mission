package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(

    @Schema(description = "로그인용 사용자 이름", example = "hyuna")
    String username,

    @Schema(description = "로그인용 비밀번호", example = "mySecret123!")
    String password
) {

}
