package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.*;

public record UserCreateRequest(
//    @NotBlank(message = "사용자 이름은 필수입니다")
//    @Size(min = 1, max = 10, message = "사용자 이름은 1~10자 사이여야 합니다")
    String username,

//    @NotBlank(message = "이메일은 필수입니다")
//    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

//    @Size(min = 8, max = 30, message = "비밀번호는 8~30자 사이여야 합니다")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
//        message = "비밀번호는 최소 하나의 영문자, 숫자 및 특수문자(@$!%*?&)를 포함해야 합니다")
    String password
) {

}
