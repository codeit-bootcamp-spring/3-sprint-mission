package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

/* 로그인 기능은 filter 로 옮김 - 사용안함 */
public record LoginRequest(
    @NotBlank(message = "이름은 필수입니다")
    String username,
    @NotBlank(message = "비밀번호는 필수입니다")
    String password) {

}

