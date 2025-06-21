package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 생성 요청 DTO")
public record UserCreateRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 2, max = 20) String username,
    @NotBlank @Size(min = 4, max = 30) String password
) {

}