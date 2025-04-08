package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JcfChannelService implements ChannelService {
  private final List<Channel> channels = new ArrayList<>();

  private final JcfUserService userService;

  //JcfChannelService에서 JcfUserService를 의존성으로 추가하고, 그 안에 저장된 유저를 가져온다.
  public JcfChannelService(JcfUserService userService) {
    this.userService = userService;
  }

  @Override
  public Channel createChannel(String channelName, User ownerUser) {
    List<User> members = new ArrayList<>();
    members.add(ownerUser);
    Channel channel = new Channel(channelName, ownerUser, members);
    //  public Channel(String channelName, User channelOwner, List<User> channelUsers) {
    channels.add(channel);
    return channel;
  }

  @Override
  public Channel getChannelById(UUID channelId) {
    return channels.stream()
        .filter(e -> e.getId().equals(channelId))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Channel> getAllChannels() {
    return new ArrayList<>(channels);
  }

  @Override
  public void updateChannelName(UUID channelId, String channelName) {
    Channel channel = getChannelById(channelId);
    // 채널이 존재해야 update가능
    channel.updateChannelName(channelName);
  }

  @Override
  public void deleteChannel(UUID channelId) {
    //channels.remove(getChannelById(channelId));
    channels.removeIf(e -> e.getId().equals(channelId));
  }

  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userService.getUserById(userId);

    if (user == null) {
      // 유저가 없으면 로그만 남기고 종료
      System.out.println("오류: addMember() 탈퇴한 test02 유저를 채널에 추가 시도 || 존재하지 않은 유저 ID(NULL)입니다. -> " + userId);
      return;
    }

    channel.addChannelUser(user);
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userService.getUserById(userId);
    channel.removeChannelUser(user);
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = getChannelById(channelId);
    return new ArrayList<>(channel.getChannelUsers());
  }

  // 유저가 만든 모든 채널 삭제
  public void deleteChannelsCreatedByUser(UUID userId) {
    channels.removeIf(channel -> channel.getChannelOwner().getId().equals(userId));
  }

  // 유저가 참여 중인 모든 채널에서 탈퇴
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channels) {
      channel.getChannelUsers().removeIf(user -> user.getId().equals(userId));
    }
  }
}
