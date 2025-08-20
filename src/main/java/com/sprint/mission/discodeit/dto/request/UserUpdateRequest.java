package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

    @Size(min = 3, max = 30, message = "사용자 이름은 3자 이상 30자 이하여야 합니다")
    String newUsername,

    @Email
    @Size(max = 70, message = "이메일은 70자 이하여야 합니다")
    String newEmail,

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$", message = "비밀번호는 8자 이상 20자 이하입니다.")
    String newPassword
) { }