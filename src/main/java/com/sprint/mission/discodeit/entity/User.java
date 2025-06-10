package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User extends BaseUpdatableEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;


  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  public User(String username, String email, String password, BinaryContent profile) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public boolean isPasswordMatch(String rawPassword) {
    return this.password.equals(rawPassword);
  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile) {
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null && !newProfile.equals(this.profile)) {
      this.profile = newProfile;
    }
    this.updatedAt = java.time.Instant.now();
  }

  protected User() {
  }
}

