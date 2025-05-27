package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;
  private UUID profileId;
  private String introduction;
  private boolean online;
  private List<UUID> friends;
  private List<UUID> channels;
  private List<UUID> messages;

  public User(String username, String email, String password, String introduction) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.introduction = introduction;
    this.online = true;
    this.friends = new ArrayList<>();
    this.channels = new ArrayList<>();
    this.messages = new ArrayList<>();
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

  public void updateIntroduction(String introduction) {
    this.introduction = introduction;
  }

  public void updateProfileID(UUID profileId) {
    this.profileId = profileId;
  }

  public void updateOnline(boolean online) {
    this.online = online;
  }

  public static UserResponseDTO toDTO(User user) {
    UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        user.getIntroduction(),
        user.isOnline(),
        user.getFriends(),
        user.getChannels(),
        user.getMessages());

    return userResponseDTO;
  }

  @Override
  public String toString() {
    return "User {\n" +
        "  id=" + getId() + ",\n" +
        "  createdAt=" + getCreatedAt() + ",\n" +
        "  updatedAt=" + getUpdatedAt() + ",\n" +
        "  username='" + username + "',\n" +
        "  email='" + email + "',\n" +
        "  password='" + password + "',\n" +
        "  introduction='" + introduction + "',\n" +
        "  online=" + online + ",\n" +
        "  friends=" + friends + ",\n" +
        "  channels=" + channels.stream().toList() + "\n" +
        "  messages=" + messages.stream().toList() + "\n" +
        '}';
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
