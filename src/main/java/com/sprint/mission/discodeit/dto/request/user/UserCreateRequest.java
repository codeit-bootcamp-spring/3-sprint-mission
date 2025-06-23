package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
    @NotBlank(message = "사용자명을 입력해주세요") String username,
    @NotBlank(message = "이메일을 입력해주세요") @Email(message = "올바른 이메일 형식으로 입력해주세요") String email,
    @NotBlank(message = "비밀번호를 입력해주세요") String password
) {
}
