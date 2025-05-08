package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.ChannelException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 4947061877205205272L;

  public enum ChannelType {
    PUBLIC,
    PRIVATE
  }

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final UUID creatorId;
  private String name;
  private String description;
  private final Set<UUID> participants = new HashSet<>();
  private final ChannelType type;

  private Channel(UUID creatorId, ChannelType type) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.creatorId = Objects.requireNonNull(creatorId);
    this.name = null;
    this.description = null;
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  private Channel(UUID creatorId, String name, String description, ChannelType type) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.creatorId = Objects.requireNonNull(creatorId);
    this.name = Objects.requireNonNull(name);
    this.description = description != null ? description : "";
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  public static Channel create(UUID creatorId, String name, String description) {
    return new Channel(creatorId, name, description, ChannelType.PUBLIC);
  }

  public static Channel createPublic(UUID creatorId, String name, String description) {
    return create(creatorId, name, description);
  }

  public static Channel createPrivate(UUID creatorId) {
    return new Channel(creatorId, ChannelType.PRIVATE);
  }

  public void touch() {
    this.updatedAt = Instant.now();
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
    if (!(o instanceof Channel channel)) {
      return false;
    }
    return Objects.equals(id, channel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
