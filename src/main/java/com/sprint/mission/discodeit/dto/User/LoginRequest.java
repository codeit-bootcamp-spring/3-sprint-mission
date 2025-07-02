package com.sprint.mission.discodeit.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "사용자 이름은 빈 값일 수 없습니다.")
        @Size(max = 50, message = "사용자 이름은 최대 50자입니다.")
        String username,

        @NotBlank(message = "비밀번호는 빈 값일 수 없습니다.")
        @Size(max = 60, message = "비밀번호는 최대 60자입니다.")
        String password
) {

}

