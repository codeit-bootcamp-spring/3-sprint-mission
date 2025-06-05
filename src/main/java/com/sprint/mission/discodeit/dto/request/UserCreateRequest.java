package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 생성 요청 DTO")
public record UserCreateRequest(
    String email,
    String username,
    String password
) {

}