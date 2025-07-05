package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 정보 관리
 * <p>
 * 공통 속성(고유 아이디, 생성/수정 시간) 관리는 {@link Base} 객체에 위임하여 컴포지션 방식으로 구현한다.
 * <ul>
 *   <li>사용자 계정 정보 (email, password, name)</li>
 *   <li>참여 채널 목록</li>
 * </ul>
 */
public class User {

  private final Base base;
  private String email;
  private String password;
  private String name;
  private List<Channel> channels = new ArrayList<>();

  // 외부에서 직접 객체 생성 방지.
  private User(String email, String name, String password) {
    this.base = new Base();
    this.email = email;
    this.password = password;
    this.name = name;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static User create(String email, String name, String password) {
    return new User(email, name, password);
  }

  // 사용자 정보 관리
  public String getEmail() {
    return email;
  }

  public void updatePassword(String password) {
    this.password = password;
    base.setUpdatedAt();
  }

  public String getName() {
    return name;
  }

  public void updateName(String name) {
    this.name = name;
    base.setUpdatedAt();
  }

  // 채널 정보 관리
  public void addChannel(Channel channel) {
    if (!channels.contains(channel)) {
      this.channels.add(channel);
      base.setUpdatedAt();
    }
  }

  public List<Channel> getChannels() {
    return new ArrayList<>(channels);
  }

  // Base 위임 메서드
  public UUID getId() {
    return base.getId();
  }

  public long getCreatedAt() {
    return base.getCreatedAt();
  }

  public long getUpdatedAt() {
    return base.getUpdatedAt();
  }

  @Override
  public String toString() {
    return "User{" +
        "email='" + email + '\'' +
        ", name='" + name + '\'' +
        ", channels=" + channels.size() +
        ", id=" + getId() +
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
    return getId().equals(user.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}