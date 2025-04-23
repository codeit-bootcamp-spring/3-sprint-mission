package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
  private static final String FILE_PATH = "channels.ser";
  private final Map<UUID, Channel> channelData;
  private final UserService userService;

  public FileChannelService(UserService userService) {
    this.userService = userService;
    this.channelData = loadChannelData();
  }

  private void saveChannelData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(channelData);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<UUID, Channel> loadChannelData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>();
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Channel>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  @Override
  public Channel createChannel(String channelName, User ownerUser) {
    boolean isDuplicate = channelData.values().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerUser.getId())
            && c.getChannelName().equals(channelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다. 다른 이름을 설정해주세요.");
    }

    List<User> members = new ArrayList<>();
    members.add(ownerUser);
    Channel channel = new Channel(channelName, ownerUser, members);
    channelData.put(channel.getId(), channel);
    saveChannelData();
    return channel;
  }

  @Override
  public Optional<Channel> getChannelById(UUID channelId) {
    return Optional.ofNullable(channelData.get(channelId));
  }

  @Override
  public List<Channel> getAllChannels() {
    return new ArrayList<>(channelData.values());
  }

  @Override
  public void updateChannelName(UUID channelId, String newChannelName) {
    Channel channel = getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    if (channel.getChannelName().equals(newChannelName)) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다.");
    }

    UUID ownerId = channel.getChannelOwner().getId();
    boolean isDuplicate = channelData.values().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(newChannelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("해당 채널명은 이미 사용 중입니다.");
    }

    channel.updateChannelName(newChannelName);
    saveChannelData();
  }

  @Override
  public void deleteChannel(UUID channelId) {
    channelData.remove(channelId);
    saveChannelData();
  }

  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.addChannelUser(user);
    saveChannelData();
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userService.getUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.removeChannelUser(user);
    saveChannelData();
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = getChannelById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    return new ArrayList<>(channel.getChannelUsers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channelData.entrySet().removeIf(entry -> entry.getValue().getChannelOwner().getId().equals(userId));
    saveChannelData();
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channelData.values()) {
      channel.getChannelUsers().removeIf(user -> user.getId().equals(userId));
    }
    saveChannelData();
  }
}