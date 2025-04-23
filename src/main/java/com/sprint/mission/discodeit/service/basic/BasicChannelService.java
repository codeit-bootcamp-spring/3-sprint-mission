package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserService userService;

  public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
    this.channelRepository = channelRepository;
    this.userService = userService;
  }

  @Override
  public Channel createChannel(String channelName, User ownerUser) {
    boolean isDuplicate = channelRepository.getAllChannels().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerUser.getId())
            && c.getChannelName().equals(channelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
    }

    List<User> members = new ArrayList<>();
    members.add(ownerUser);

    Channel channel = new Channel(channelName, ownerUser, members);
    return channelRepository.save(channel);
  }

  @Override
  public Optional<Channel> getChannelById(UUID channelId) {
    return channelRepository.getChannelById(channelId);
  }

  @Override
  public List<Channel> getAllChannels() {
    return channelRepository.getAllChannels();
  }

  @Override
  public void updateChannelName(UUID channelId, String newChannelName) {
    Channel channel = channelRepository.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    if (channel.getChannelName().equals(newChannelName)) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다.");
    }

    UUID ownerId = channel.getChannelOwner().getId();
    boolean isDuplicate = channelRepository.getAllChannels().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(newChannelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 해당 채널명이 존재합니다.");
    }

    channel.updateChannelName(newChannelName);
    channelRepository.update(channel);
  }

  @Override
  public void deleteChannel(UUID channelId) {
    channelRepository.delete(channelId);
  }

  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = channelRepository.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.addChannelUser(user);
    channelRepository.update(channel);
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = channelRepository.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.removeChannelUser(user);
    channelRepository.update(channel);
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = channelRepository.getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    return new ArrayList<>(channel.getChannelUsers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channelRepository.deleteByOwnerId(userId);
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    channelRepository.removeUserFromAllChannels(userId);
  }
}
