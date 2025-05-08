package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
  private final List<Channel> channels = new ArrayList<>();
  private UUID profileImageId;

  private User(String email, String name, String password, UUID profileImageId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.email = email;
    this.name = name;
    this.password = password;
    this.profileImageId = profileImageId;
  }

  public static User create(String email, String name, String password) {
    return new User(email, name, password, null);
  }

  public static User create(String email, String name, String password, UUID profileImageId) {
    return new User(email, name, password, profileImageId);
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updatePassword(String password) {
    this.password = password;
    touch();
  }

  public void updateName(String name) {
    this.name = name;
    touch();
  }

  // 채널 정보 관리
  public void addChannel(Channel channel) {
    if (!channels.contains(channel)) {
      this.channels.add(channel);
      touch();
    }
  }

  public List<Channel> getChannels() {
    return new ArrayList<>(channels);
  }

  public void updateProfileImageId(UUID profileImageId) {
    this.profileImageId = profileImageId;
    touch();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}