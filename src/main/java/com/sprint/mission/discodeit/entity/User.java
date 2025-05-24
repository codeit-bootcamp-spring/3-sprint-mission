package com.sprint.mission.discodeit.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 사용자 << 공통 필드를 가진 Base 추상화 클래스 상속

// Lombok( 해당 클래스의 필드에만 적용됨 | Getter 메서드를 사용하던 시점에 비해 코드 가독성 증진 )
@Getter
@Schema(description = "사용자 정보 도메인 모델")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  // 필드 정의
  @Schema(
      description = "사용자 고유 식별 번호",
      example = "550e8400-e29b-41d4-a716-446655440000",
      type = "string",
      format = "uuid"
  )
  private final UUID userId;

  @Schema(
      description = "사용자 생성 시각",
      example = "2025-05-13T00:00:00Z",
      type = "string",
      format = "date-time"
  )
  private final Instant createdAt;

  @Schema(
      description = "사용자 정보 수정 시각",
      example = "2025-05-15T01:23:00Z",
      type = "string",
      format = "date-time"
  )
  private Instant updatedAt;

  @Schema(
      description = "사용자 이름",
      example = "홍길동",
      type = "string"
  )
  @NotBlank(message = "사용자 이름은 필수입니다")
  private String userName;


  @Schema(
      description = "이메일 주소",
      example = "test@example.com",
      type = "string",
      format = "email"
  )
  @NotBlank(message = "이메일은 필수입니다")
  @Email(message = "올바른 이메일 형식으로 작성하여 주세요")
  private String email;

  @Schema(
      description = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 포함한 8자 이상이어야합니다",
      example = "Qwerty12!@",
      type = "string",
      format = "password"
  )
  @NotBlank(message = "비밀번호를 입력해주세요")
  private String password;

  // BinaryContent 참조 ID
  @Schema(
      description = "등록 가능한 프로필 이미지의 고유 식별 번호",
      example = "0bd1a8d0-643e-43e5-9fcb-65123987ec2a0",
      type = "string",
      format = "uuid",
      nullable = true
  )
  private UUID profileId;

  // 생성자
  public User(String userName, String email, String password, UUID profileId) {
    this.userId = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.userName = userName;
    this.email = email;
    this.password = password;
    //
    this.profileId = profileId;
  }


  // Update
  public void update(String newUserName, String newPassword, String newEmail) {
    boolean updated = false;
    if (newUserName != null && !newUserName.equals(this.userName)) {
      this.userName = newUserName;
      updated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      updated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      updated = true;
    }
    if (updated) {
      this.updatedAt = Instant.now();
    } else {
      // 예외 처리
      throw new IllegalArgumentException("No field to update");
    }
  }

}