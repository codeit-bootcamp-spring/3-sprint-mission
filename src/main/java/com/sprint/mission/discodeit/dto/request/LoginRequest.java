package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 계정 로그인")
public final class LoginRequest {

  @Schema(description = "로그인하고자 하는 유저명")
  private String username;

  @Schema(description = "로그인하고자 하는 계정의 비밀번호")
  private String password;
}
