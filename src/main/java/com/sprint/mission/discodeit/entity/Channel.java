package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.exception.ChannelException;
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

  private String description;
  private final User creator;
  private String name;
  private final List<User> participants = new ArrayList<>();
  private final ChannelType type;

  private Channel(String description, User creator, String name, ChannelType type) {
    this.description = description != null ? description : "";
    this.creator = Objects.requireNonNull(creator);
    this.name = Objects.requireNonNull(name);
    this.type = type != null ? type : ChannelType.PUBLIC;
    this.participants.add(creator);
  }

  public static Channel create(User creator, String name, String description) {
    Channel channel = new Channel(description, creator, name, ChannelType.PUBLIC);
    channel.touch();
    return channel;
  }

  public static Channel createPublic(User creator, String name, String description) {
    Channel channel = new Channel(description, creator, name, ChannelType.PUBLIC);
    channel.touch();
    return channel;
  }

  public static Channel createPrivate(User creator, String name, String description) {
    Channel channel = new Channel(description, creator, name, ChannelType.PRIVATE);
    channel.touch();
    return channel;
  }

  public void updateName(String name) {
    this.name = name;
    touch();
  }

  public void addParticipant(User user) throws ChannelException {
    if (participants.contains(user)) {
      throw ChannelException.participantAlreadyExists(user.getId(), this.getId());
    }
    participants.add(user);
    touch();
  }

  public List<User> getParticipants() {
    return List.copyOf(participants);
  }

  public void removeParticipant(UUID userId) throws ChannelException {
    User participantToRemove = participants.stream()
        .filter(user -> user.getId().equals(userId))
        .findFirst()
        .orElseThrow(() -> ChannelException.participantNotFound(userId, this.getId()));

    participants.remove(participantToRemove);
    touch();
  }

  public boolean isParticipant(UUID userId) {
    return participants.stream()
        .anyMatch(participant -> participant.getId().equals(userId));
  }

  public boolean isNotParticipant(UUID userId) {
    return !isParticipant(userId);
  }

  public boolean matchesFilter(UUID creatorId, String name) {
    boolean matchesCreator = (creatorId == null || this.creator.getId().equals(creatorId));
    boolean matchesName = (name == null || this.name.contains(name));
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