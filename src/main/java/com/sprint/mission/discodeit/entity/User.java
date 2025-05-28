package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users", schema = "discodeit")
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @OneToOne
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<ReadStatus> readStatuses;

  public User() {
  }

  public User(String username, String email, String password, BinaryContent profile, UserStatus status) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.status = status;
  }

  public void updateName(String username) {
    this.username = username;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }

  public void updateStatus(UserStatus status) {
    this.status = status;
  }

//  public static UserResponseDTO toDTO(User user) {
//    UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(),
//        user.getCreatedAt(),
//        user.getUpdatedAt(),
//        user.getUsername(),
//        user.getEmail(),
//        user.getProfile(),
//        user.getStatus(),
//        user.getFriends(),
//        user.getChannels(),
//        user.getMessages());
//
//    return userResponseDTO;
//  }

  @Override
  public String toString() {
    return "User{" +
        "password='" + password + '\'' +
        ", email='" + email + '\'' +
        ", username='" + username + '\'' +
        "} " + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
