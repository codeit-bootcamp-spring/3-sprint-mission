package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 수정 요청 DTO")
public record UserUpdateRequest(
    String newName,
    String newPassword
) {

}