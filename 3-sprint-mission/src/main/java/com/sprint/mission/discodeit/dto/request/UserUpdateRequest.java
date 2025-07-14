package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(max = 50, message = "사용자 이름은 50자 이하로 입력해주세요.")
    String newUsername,
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    @Email
    String newEmail,
    @Size(max = 60, message = "비밀번호는 60자 이하로 입력해주세요.")
    String newPassword
) {

}
