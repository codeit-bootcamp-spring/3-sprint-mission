package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
    String username,
    String password
) {

}
