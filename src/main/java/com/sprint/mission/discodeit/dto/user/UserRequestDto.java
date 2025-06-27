package com.sprint.mission.discodeit.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User 생성 및 수정 정보")
public record UserRequestDto(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 10, message = "이름은 최대 10자까지 입력 가능합니다.")
        String username,

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하로 입력해주세요")
        String password) {

}
