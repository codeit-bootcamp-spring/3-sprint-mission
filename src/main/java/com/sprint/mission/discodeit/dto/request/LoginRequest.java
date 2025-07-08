package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "사용자명을 입력해주세요") String username,
    @NotBlank(message = "비밀번호를 입력해주세요") String password
) {
}
