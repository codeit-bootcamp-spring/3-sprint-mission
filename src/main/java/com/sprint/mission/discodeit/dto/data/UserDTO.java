package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "사용자 정보를 담는 DTO")
public class UserDTO {

  @Schema(description = "사용자 ID", type = "string", format = "uuid")
  private UUID id;

  @Schema(description = "사용자 정보가 생성된 시각", type = "string", format = "date-time")
  private Instant createdAt;

  @Schema(description = "사용자 정보가 수정된 시각", type = "string", format = "date-time")
  private Instant updateAt;

  @Schema(description = "사용자 이름", example = "홍길동")
  private String username;

  @Schema(description = "사용자 이메일", example = "example@test.com", type = "string", format = "email")
  private String email;

  @Schema(description = "프로필 이미지 파일 ID", type = "string", format = "uuid")
  private UUID profileId;

  @Schema(description = "사용자의 온라인 상태", example = "온라인", type = "string", format = "boolean")
  private Boolean online;
}
