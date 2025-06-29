package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "새 사용자 이름을 입력해주세요.")
    String newUsername,

    @NotBlank(message = "새 이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String newEmail,

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    String newPassword
) {

}
