package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "변경할 사용자 비밀번호 정보")
// 보안성 향상의 목적으로 pwd update 별도 관리
public final class UserPasswordUpdateRequest {

  @Schema(description = "변경할 사용자 계정의 현재 비밀번호", type = "string", format = "password")
  String currentPassword;

  @Schema(description = "변경될 사용자 계정의 새로운 비밀번호", type = "string", format = "password")
  String newPassword;
}
