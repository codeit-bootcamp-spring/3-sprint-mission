package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 수정 요청 DTO")
public record UserUpdateRequest(
        @NotBlank @Size(min = 2, max = 20) String newUsername,
        @NotBlank @Email String newEmail,
        @NotBlank @Size(min = 8, max = 30) String newPassword) {

}