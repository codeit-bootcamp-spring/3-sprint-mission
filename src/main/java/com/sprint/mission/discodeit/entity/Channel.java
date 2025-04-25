package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.ChannelException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

/**
 * 채널 정보 관리
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt, updatedAt)</li>
 *   <li>채널명</li>
 *   <li>생성자</li>
 *   <li>참여자 목록</li>
 * </ul>
 */
@Getter
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 4947061877205205272L;
  // 채널 정보 관리
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  // 참조 정보 getter
  private final User creator;
  private String name;
  private List<User> participants = new ArrayList<>();

  // 외부에서 직접 객체 생성 방지.
  private Channel(User creator, String name) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.creator = creator;
    this.name = name;
    this.participants.add(creator);
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static Channel create(User creator, String name) {
    return new Channel(creator, name);
  }

  public void setUpdatedAt() {
    this.updatedAt = Instant.now();
  }

  public void updateName(String name) {
    this.name = name;
    setUpdatedAt();
  }

  // 참여자 관리
  public void addParticipant(User user) throws ChannelException {
    if (participants.contains(user)) {
      throw ChannelException.participantAlreadyExists(user.getId(), this.getId());
    }
    participants.add(user);
    setUpdatedAt();
  }

  public List<User> getParticipants() {
    return new ArrayList<>(participants);
  }

  public void removeParticipant(UUID userId) throws ChannelException {
    User participantToRemove = participants.stream()
        .filter(user -> user.getId().equals(userId))
        .findFirst()
        .orElseThrow(() -> ChannelException.participantNotFound(userId, this.getId()));

    participants.remove(participantToRemove);
    setUpdatedAt();
  }

  @Override
  public String toString() {
    return "Channel{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
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
    return Objects.equals(id, channel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}