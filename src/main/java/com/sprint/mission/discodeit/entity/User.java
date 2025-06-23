package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.InvalidInputException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus userStatus;

  private User(String email, String username, String password, BinaryContent profile) {
    validate(email, username, password);

    this.email = email;
    this.username = username;
    this.password = password;
    this.profile = profile;
  }

  private static void validate(String email, String name, String password) {
    if (email == null || email.isBlank()) {
      throw new InvalidInputException("이메일은 비어 있을 수 없습니다.");
    }
    if (name == null || name.isBlank()) {
      throw new InvalidInputException("이름은 비어 있을 수 없습니다.");
    }
    if (password == null || password.isBlank()) {
      throw new InvalidInputException("비밀번호는 비어 있을 수 없습니다.");
    }
  }

  public static User create(String email, String name, String password, BinaryContent profile) {
    return new User(email, name, password, profile);
  }

  public void assignIdForTest(UUID id) {
    this.id = id;
  }

  public void updatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new InvalidInputException("비밀번호는 비어 있을 수 없습니다.");
    }
    this.password = password;
  }

  public void updateName(String name) {
    if (name == null || name.isBlank()) {
      throw new InvalidInputException("이름은 비어 있을 수 없습니다.");
    }
    this.username = name;
  }

  public void updateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new InvalidInputException("이메일은 비어 있을 수 없습니다.");
    }
    this.email = email;
  }

  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }

  public void updateUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
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