package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 계정 로그인")
public final class LoginRequest {

  @Schema(description = "로그인하고자 하는 유저명")
  @NotBlank(message = "로그인할 계정의 사용자 이름을 작성해주세요")
  private String userName;
  @Schema(description = "로그인하고자 하는 계정의 비밀번호")
  @NotBlank(message = "로그인할 계정의 비밀번호를 작성해주세요")
  private String pwd;
}
