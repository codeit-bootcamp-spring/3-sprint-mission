package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 관리
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt, updatedAt)</li>
 *   <li>사용자 계정 정보 (email, name, password)</li>
 *   <li>참여 채널 목록</li>
 * </ul>
 */
@Getter
@Builder
public class User implements Serializable {

  @Serial
  private static final long serialVersionUID = 8019397210486307690L;
  // 사용자 정보 관리
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private final String email;
  private String name;
  private String password;
  private final List<Channel> channels = new ArrayList<>();
  private UUID profileImageId;

  public static User create(String email, String name, String password) {
    return User.builder()
        .email(email)
        .name(name)
        .password(password)
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  public static User create(String email, String name, String password, UUID profileImageId) {
    return User.builder()
        .email(email)
        .name(name)
        .password(password)
        .profileImageId(profileImageId)
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  public void setUpdatedAt() {
    this.updatedAt = Instant.now();
  }

  public void updatePassword(String password) {
    this.password = password;
    setUpdatedAt();
  }

  public void updateName(String name) {
    this.name = name;
    setUpdatedAt();
  }

  // 채널 정보 관리
  public void addChannel(Channel channel) {
    if (!channels.contains(channel)) {
      this.channels.add(channel);
      setUpdatedAt();
    }
  }

  public List<Channel> getChannels() {
    return new ArrayList<>(channels);
  }

  public void updateProfileImageId(UUID profileImageId) {
    this.profileImageId = profileImageId;
    setUpdatedAt();
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", email='" + email + '\'' +
        ", name='" + name + '\'' +
        ", password='" + password + '\'' +
        ", channels=" + channels +
        '}';
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
    return Objects.equals(id, user.id) &&
        Objects.equals(email, user.email) &&
        Objects.equals(name, user.name) &&
        Objects.equals(password, user.password) &&
        Objects.equals(createdAt, user.createdAt) &&
        Objects.equals(updatedAt, user.updatedAt) &&
        Objects.equals(channels, user.channels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, password, createdAt, updatedAt, channels);
  }
}