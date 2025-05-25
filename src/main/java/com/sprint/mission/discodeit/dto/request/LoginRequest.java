package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 계정 로그인")
public record LoginRequest(
    @Schema(description = "로그인하고자 하는 유저명")
    String username,

    @Schema(description = "로그인하고자 하는 계정의 비밀번호")
    String password
) {

}
