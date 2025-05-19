package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private String username;
  private String email;
  private String password;
  private UUID profileId;
  private String introduction;
  private boolean isLogin;
  private List<UUID> friends;
  private List<UUID> channels;
  private List<UUID> messages;

  public User(String username, String email, String password, String introduction) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.username = username;
    this.email = email;
    this.password = password;
    this.introduction = introduction;
    this.isLogin = true;
    this.friends = new ArrayList<>();
    this.channels = new ArrayList<>();
    this.messages = new ArrayList<>();
  }

  public void updateName(String username) {
    this.username = username;
    this.updatedAt = Instant.now();
  }

  public void updateEmail(String email) {
    this.email = email;
    this.updatedAt = Instant.now();
  }

  public void updatePassword(String password) {
    this.password = password;
    this.updatedAt = Instant.now();
  }

  public void updateIntroduction(String introduction) {
    this.introduction = introduction;
    this.updatedAt = Instant.now();
  }

  public void updateProfileID(UUID profileId) {
    this.profileId = profileId;
    this.updatedAt = Instant.now();
  }

  public void updateisLogin(boolean isLogin) {
    this.isLogin = isLogin;
  }

  public static UserResponseDTO toDTO(User user) {
    UserResponseDTO userResponseDTO = new UserResponseDTO(user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        user.getIntroduction(),
        user.isLogin(),
        user.getFriends(),
        user.getChannels(),
        user.getMessages());

    return userResponseDTO;
  }

  @Override
  public String toString() {
    return "User {\n" +
        "  id=" + id + ",\n" +
        "  createdAt=" + createdAt + ",\n" +
        "  updatedAt=" + updatedAt + ",\n" +
        "  username='" + username + "',\n" +
        "  email='" + email + "',\n" +
        "  password='" + password + "',\n" +
        "  introduction='" + introduction + "',\n" +
        "  isLogin=" + isLogin + ",\n" +
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
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
