package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 업데이트 요청 DTO")
public record UserUpdateRequest(
        @Schema(description = "새로운 사용자 이름", example = "john_doe_new", nullable = true)
        String newUsername,

        @Schema(description = "새로운 이메일 주소", example = "john.new@example.com", format = "email", nullable = true)
        String newEmail,

        @Schema(description = "새로운 비밀번호", example = "newpassword123", nullable = true)
        String newPassword
) {
}
