package com.sprint.mission.discodeit.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(max = 50, message = "사용자명은 50자 이하여야 합니다")
    String username,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(max = 60, message = "비밀번호는 60자 이하여야 합니다")
    String password
) {

}
