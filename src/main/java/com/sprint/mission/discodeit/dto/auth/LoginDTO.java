package com.sprint.mission.discodeit.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 정보")
public record LoginDTO(String username, String password) {

}
