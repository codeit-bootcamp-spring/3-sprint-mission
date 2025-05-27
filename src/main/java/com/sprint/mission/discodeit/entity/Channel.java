package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private String name;
  private String description;
  private User channelOwner;
  private List<User> channelMembers;
  private ChannelType type;

  public Channel(String name, String description, User channelOwner, List<User> channelUsers,
      ChannelType type) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.name = name;
    this.description = description;
    this.channelOwner = channelOwner;
    this.channelMembers = channelUsers;
    this.type = type;
  }

  public Channel(ChannelType type, String name, String description) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void updateChannelName(String channelName) {
    this.name = channelName;
    this.updatedAt = Instant.now();
  }

  @Override
  public String toString() {
    return "[" +
        "createdAt= " + createdAt +
        ", updatedAt= " + updatedAt +
        ", channelName=" + name +
        ", channelOwner=" + channelOwner.getUsername() +
        ']';
  }
}
