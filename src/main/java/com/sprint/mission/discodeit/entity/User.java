package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.model.Auditable;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자 정보 관리
 * <p>
 * <ul>
 * <li>AuditInfo (id, createdAt, updatedAt)</li>
 * <li>사용자 계정 정보 (email, name, password)</li>
 * <li>참여 채널 목록</li>
 * </ul>
 */
@Getter
@ToString(callSuper = true)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class User extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = 8019397210486307690L;
  // 사용자 계정 정보
  private final String email;
  private String name;
  private String password;
  private final List<Channel> channels = new ArrayList<>();
  private UUID profileImageId;

  private User(String email, String name, String password, UUID profileImageId) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.profileImageId = profileImageId;
  }

  public static User create(String email, String name, String password) {
    User user = new User(email, name, password, null);
    user.touch();
    return user;
  }

  public static User create(String email, String name, String password, UUID profileImageId) {
    User user = new User(email, name, password, profileImageId);
    user.touch();
    return user;
  }

  @Override
  public void touch() {
    super.touch();
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