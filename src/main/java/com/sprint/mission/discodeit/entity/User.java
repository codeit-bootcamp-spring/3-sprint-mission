package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.Objects;
import lombok.Getter;

@Getter
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;
  private BinaryContent profile;
  private UserStatus status;

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
