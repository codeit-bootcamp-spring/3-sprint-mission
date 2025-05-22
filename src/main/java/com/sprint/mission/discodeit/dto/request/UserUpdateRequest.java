package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateRequest(
    @Schema(description = "새로운 사용자 이름", example = "쪼현아")
    String newUsername,

    @Schema(description = "새로운 이메일 주소", example = "hyuna_updated@example.com")
    String newEmail,

    @Schema(description = "새로운 비밀번호", example = "newSecurePassword123!")
    String newPassword
) {

}
