package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "User",
        description = "사용자 정보를 담고 있는 엔티티"
)
@Getter
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(
          description = "사용자의 고유 식별자",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000"
  )
  private UUID id;

  @Schema(
          description = "사용자 계정 생성 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant createdAt;

  @Schema(
          description = "사용자 정보 최종 수정 시간",
          type = "string",
          format = "date-time",
          example = "2024-03-20T09:12:28Z"
  )
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant updatedAt;

  @Schema(
          description = "사용자 이름",
          type = "string",
          example = "john_doe",
          minLength = 3,
          maxLength = 50
  )
  private String username;

  @Schema(
          description = "사용자 이메일 주소",
          type = "string",
          format = "email",
          example = "john.doe@example.com"
  )
  private String email;

  @Schema(
          description = "사용자 비밀번호 (해시된 값)",
          type = "string",
          format = "password",
          example = "********"
  )
  private String password;

  @Schema(
          description = "사용자 프로필 이미지의 ID",
          type = "string",
          format = "uuid",
          example = "123e4567-e89b-12d3-a456-426614174000",
          nullable = true
  )
  private UUID profileId;


  public User(String username, String email, String password, UUID profileId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

  public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }
    if (newProfileId != null && !newProfileId.equals(this.profileId)) {
      this.profileId = newProfileId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
