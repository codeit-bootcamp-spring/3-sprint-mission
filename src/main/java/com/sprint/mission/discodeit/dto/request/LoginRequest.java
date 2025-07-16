package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "사용자 이름은 필수입니다.")
    String username,

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    String password
) {

}
