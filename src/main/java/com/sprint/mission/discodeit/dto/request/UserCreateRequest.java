package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.common.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "사용자 이름은 필수입니다.") @Size(max = Constants.Database.USERNAME_MAX_LENGTH, message = "사용자 이름은 50자를 초과할 수 없습니다.") String username,

    @NotBlank(message = "이메일은 필수입니다.") @Email(message = "올바른 이메일 형식이 아닙니다.") @Size(max = Constants.Database.EMAIL_MAX_LENGTH, message = "이메일은 100자를 초과할 수 없습니다.") String email,

    @NotBlank(message = "비밀번호는 필수입니다.") @Size(min = 8, max = Constants.Database.PASSWORD_MAX_LENGTH, message = "비밀번호는 8자 이상 60자 이하여야 합니다.") String password) {

}
