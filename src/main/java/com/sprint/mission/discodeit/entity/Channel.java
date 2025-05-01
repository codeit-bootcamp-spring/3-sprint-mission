package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.common.model.Auditable;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class Channel extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = 4947061877205205272L;

  public enum ChannelType {
    PUBLIC,
    PRIVATE
  }

  private final UUID creatorId;
  private String name;
  private String description;
  private final Set<UUID> participants = new HashSet<>();
  private final ChannelType type;

  // 생성자에서 참여자 추가하지 않음
  private Channel(UUID creatorId, ChannelType type) {
    this.creatorId = Objects.requireNonNull(creatorId);
    this.name = null;
    this.description = null;
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  private Channel(UUID creatorId, String name, String description, ChannelType type) {
    this.creatorId = Objects.requireNonNull(creatorId);
    this.name = Objects.requireNonNull(name);
    this.description = description != null ? description : "";
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  public static Channel create(UUID creatorId, String name, String description) {
    Channel channel = new Channel(creatorId, name, description, ChannelType.PUBLIC);
    channel.touch();
    return channel;
  }

  public static Channel createPublic(UUID creatorId, String name, String description) {
    return create(creatorId, name, description);
  }

  public static Channel createPrivate(UUID creatorId) {
    Channel channel = new Channel(creatorId, ChannelType.PRIVATE);
    channel.touch();
    return channel;
  }

  public void updateName(String name) {
    this.name = name;
    touch();
  }

  public void updateDescription(String description) {
    this.description = description;
    touch();
  }

  public void addParticipant(UUID userId) throws ChannelException {
    if (participants.contains(userId)) {
      throw ChannelException.participantAlreadyExists(userId, this.getId());
    }
    participants.add(userId);
    touch();
  }

  public List<UUID> getParticipants() {
    return List.copyOf(participants);
  }

  public void removeParticipant(UUID userId) throws ChannelException {
    if (!participants.contains(userId)) {
      throw ChannelException.participantNotFound(userId, this.getId());
    }
    participants.remove(userId);
    touch();
  }

  public boolean isParticipant(UUID userId) {
    return participants.contains(userId);
  }

  public boolean isNotParticipant(UUID userId) {
    return !isParticipant(userId);
  }

  public boolean matchesFilter(UUID creatorId, String name) {
    boolean matchesCreator = (creatorId == null || this.creatorId.equals(creatorId));
    boolean matchesName = (name == null || (this.name != null && this.name.contains(name)));
    return matchesCreator && matchesName;
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
    return Objects.equals(getId(), channel.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
