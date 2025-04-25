package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
public class User implements Serializable {

  @Serial
  private static final long serialVersionUID = 8019397210486307690L;
  // 사용자 정보 관리
  private final UUID id;
  private final long createdAt;
  private long updatedAt;
  private String email;
  private String password;
  private String name;
  private List<Channel> channels = new ArrayList<>();

  // 외부에서 직접 객체 생성 방지.
  private User(String email, String name, String password) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.email = email;
    this.password = password;
    this.name = name;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static User create(String email, String name, String password) {
    return new User(email, name, password);
  }

  public void setUpdatedAt() {
    this.updatedAt = System.currentTimeMillis();
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
        Objects.equals(password, user.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email, name, password);
  }
}