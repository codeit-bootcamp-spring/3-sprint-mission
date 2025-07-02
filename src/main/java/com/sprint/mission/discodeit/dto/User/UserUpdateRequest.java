package com.sprint.mission.discodeit.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        //@NotBlank(message = "사용자 이름은 빈 값일 수 없습니다.")
        @Size(max = 50, message = "사용자 이름은 최대 50자입니다.")
        String newUsername,

        //@NotBlank(message = "이메일은 빈 값일 수 없습니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "이메일은 최대 100자입니다.")
        String newEmail,

        //@NotBlank(message = "비밀번호는 빈 값일 수 없습니다.")
        @Size(max = 60, message = "비밀번호는 최대 60자입니다.")
        String newPassword
) {

}
