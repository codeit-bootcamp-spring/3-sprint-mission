package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "변경할 사용자 정보")
public final class UserUpdateRequest {

  @Schema(description = "변경할 사용자 이름")
  private String newUsername;

  @Schema(description = "변경할 사용자의 이메일", type = "string", format = "email")
  private String newEmail;

  @Schema(description = "변경할 사용자의 비밀번호", type = "string", format = "password")
  private String newPassword;
}
