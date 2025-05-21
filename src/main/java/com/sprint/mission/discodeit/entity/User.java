package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class User implements Serializable {

  @Serial
  private static final long serialVersionUID = 8019397210486307690L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final String email;
  private String name;
  private String password;
  private UUID profileId;

  private User(String email, String name, String password, UUID profileId) {
    validate(email, name, password);

    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.email = email;
    this.name = name;
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
    this.name = name;
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