package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    String username,

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    String password
) {

}
