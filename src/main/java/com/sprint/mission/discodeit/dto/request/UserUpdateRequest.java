package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest (

        @Size(min = 2, max = 20, message = "사용자 명은 2자 이상 20자 이하여야 합니다.")
        @Pattern(regexp = "^([a-zA-Z]+|[가-힣]+)$", message = "사용자명은 한글 또는 영문만 사용 가능합니다.")
        String newUsername,

        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Size(max = 50, message = "이메일은 50자를 초과할 수 없습니다.")
        String newEmail,

        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다."
        )
        String newPassword
) {}
