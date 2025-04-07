package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Channel {
  private final UUID id;
  private final Long createdAt;
  private Long updatedAt;

  private String channelName;
  private User channelOwner;
  private List<User> channelUsers;

  public Channel(String channelName, User channelOwner, List<User> channelUsers) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = System.currentTimeMillis();
    this.channelName = channelName;
    this.channelOwner = channelOwner;
    this.channelUsers = channelUsers;
  }

  public void updateChannelName(String channelName) {
    this.channelName = channelName;
    this.updatedAt = System.currentTimeMillis();
  }

//  public void updateChannelOwner(User channelOwner) {
//    this.channelOwner = channelOwner;
//    this.updatedAt = System.currentTimeMillis();
//    // 예외: 채널에 포함된 멤버가 아니라면 채널소유자가 될 수 없다.
//  }

  public void addChannelUser(User user) {
    this.channelUsers.add(user);
    // 존재하는 유저인지?
  }

  public void removeChannelUser(User user) {
    this.channelUsers.remove(user);
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
