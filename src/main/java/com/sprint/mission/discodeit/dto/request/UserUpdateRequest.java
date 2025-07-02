package com.sprint.mission.discodeit.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
    @NotBlank(message = "이름은 필수입니다.")
    String name,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    String email

    String newUsername,
    String newEmail,
    String newPassword
) {

}
