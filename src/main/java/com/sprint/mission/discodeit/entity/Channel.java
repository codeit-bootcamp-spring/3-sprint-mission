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

  private String channelName;
  private String description;
  private User channelOwner;
  private List<User> channelMembers;
  private ChannelType channelType;

  /*public Channel(String channelName, String description, User channelOwner, List<User> channelUsers,
      ChannelType channelType) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.channelName = channelName;
    this.description = description;
    this.channelOwner = channelOwner;
    this.channelMembers = channelUsers;
    this.channelType = channelType;
  }*/

  public Channel(String channelName, String description, ChannelType channelType) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.channelName = channelName;
    this.description = description;
    this.channelType = channelType;
  }

  public void updateChannelName(String channelName) {
    this.channelName = channelName;
    this.updatedAt = Instant.now();
  }

  public void addChannelUser(User user) {
    this.channelMembers.add(user);
    // 존재하는 유저인지?
  }

  public void removeChannelUser(User user) {
    this.channelMembers.remove(user);
    // 채널에 포함된 유저인지?  or 채널 소유자 인지?  채널소유자는 본인의 채널이 삭제되기전에는 채널을 나갈 수 없다.
  }

  @Override
  public String toString() {
    return "[" +
        "createdAt= " + createdAt +
        ", updatedAt= " + updatedAt +
        ", channelName=" + channelName +
        ", channelOwner=" + channelOwner.getUsername() +
        ']';
  }
}
