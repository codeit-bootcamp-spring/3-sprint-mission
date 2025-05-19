package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "변경할 사용자 정보")
public final class UserUpdateRequest {

  @Schema(description = "변경할 사용자 이름")
  private String userName;

  // pwd 필드는 별도의 dto로 표현

  @Schema(description = "변경할 사용자의 이메일", type = "string", format = "email")
  @NotBlank(message = "이메일을 작성해주세요")
  private String email;

  @Schema(description = "변경할 사용자의 전화번호", type = "string", format = "phone")
  @NotBlank(message = "전화번호를 입력해주세요")
  private String phoneNumber;

  @Schema(description = "변경할 사용자의 상태 메세지")
  @NotBlank(message = "변경할 상태 메세지를 작성해주세요")
  private String StatusMessage;
}
