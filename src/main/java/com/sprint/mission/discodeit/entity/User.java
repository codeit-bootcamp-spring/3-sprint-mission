package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "profile_id")
  private UUID profileId;

  private User(String email, String username, String password, UUID profileId) {
    validate(email, username, password);

    this.email = email;
    this.username = username;
    this.password = password;
    this.profileId = profileId;
  }

  private static void validate(String email, String name, String password) {
    if (email == null || email.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "이메일은 비어 있을 수 없습니다.");
    }
    if (name == null || name.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "이름은 비어 있을 수 없습니다.");
    }
    if (password == null || password.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "비밀번호는 비어 있을 수 없습니다.");
    }
  }

  public static User create(String email, String name, String password) {
    return new User(email, name, password, null);
  }

  public static User create(String email, String name, String password, UUID profileId) {
    return new User(email, name, password, profileId);
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "비밀번호는 비어 있을 수 없습니다.");
    }
    this.password = password;
    touch();
  }

  public void updateName(String name) {
    if (name == null || name.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "이름은 비어 있을 수 없습니다.");
    }
    this.username = name;
    touch();
  }

  public void updateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new UserException(ErrorCode.INVALID_INPUT, "이메일은 비어 있을 수 없습니다.");
    }
    this.email = email;
    touch();
  }

  public void updateProfileId(UUID profileId) {
    this.profileId = profileId;
    touch();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}