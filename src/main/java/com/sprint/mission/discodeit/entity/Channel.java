package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 채널 정보 관리
 * <p>
 * 공통 속성(고유 아이디, 생성/수정 시간) 관리는 {@link Base} 객체에 위임하여 컴포지션 방식으로 구현한다.
 * <ul>
 *   <li>채널명</li>
 *   <li>생성자</li>
 *   <li>참여자 목록</li>
 * </ul>
 */
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final Base base;
  private final User creator;
  private String name;
  private List<User> participants = new ArrayList<>();

  // 외부에서 직접 객체 생성 방지.
  private Channel(User creator, String name) {
    this.base = new Base();
    this.creator = creator;
    this.name = name;
    this.participants.add(creator);
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static Channel create(User creator, String name) {
    return new Channel(creator, name);
  }

  // 채널 정보 관리
  public String getName() {
    return name;
  }

  public void updateName(String name) {
    this.name = name;
    base.setUpdatedAt();
  }

  // 참여자 관리
  public boolean addParticipant(User user) {
    if (!participants.contains(user)) {
      participants.add(user);
      base.setUpdatedAt();
      return true;
    }
    return false;
  }

  public List<User> getParticipants() {
    return new ArrayList<>(participants);
  }

  public boolean removeParticipant(UUID userId) {
    boolean removed = participants.removeIf(user -> user.getId().equals(userId));
    if (removed) {
      base.setUpdatedAt();
    }
    return removed;
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

  // 참조 정보 getter
  public User getCreator() {
    return creator;
  }

  @Override
  public String toString() {
    return "Channel{" +
        "id=" + getId() +
        ", createdAt=" + getCreatedAt() +
        ", updatedAt=" + getUpdatedAt() +
        ", name='" + name + '\'' +
        ", creator=" + creator.getName() +
        ", participants=" + participants +
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
    Channel channel = (Channel) o;
    return getId().equals(channel.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}