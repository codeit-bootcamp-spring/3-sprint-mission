package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

  private final String FILE_PATH;

  private FileChannelService(String filePath) {
    FILE_PATH = filePath;
  }

  public static FileChannelService from(String filePath) {
    return new FileChannelService(filePath);
  }

  public static FileChannelService createDefault() {
    return new FileChannelService("data/channels.ser");
  }

  @Override
  public Channel createChannel(User creator, String name) {
    Map<UUID, Channel> channelsRepository = loadData();
    Channel channel = Channel.create(creator, name);
    channelsRepository.put(channel.getId(), channel);
    saveData(channelsRepository);
    return channel;
  }

  @Override
  public Optional<Channel> getChannelById(UUID id) {
    Map<UUID, Channel> channelsRepository = loadData();
    return Optional.ofNullable(channelsRepository.get(id));
  }

  @Override
  public List<Channel> searchChannels(UUID creatorId, String name) {
    Map<UUID, Channel> channelsRepository = loadData();
    return channelsRepository.values().stream()
        .filter(channel -> (creatorId == null || channel.getCreator().getId().equals(creatorId))
            && (name == null || channel.getName().contains(name)))
        .collect(Collectors.toList());
  }

  @Override
  public List<Channel> getUserChannels(UUID userId) {
    Map<UUID, Channel> channelsRepository = loadData();
    return channelsRepository.values().stream()
        .filter(channel -> channel.getParticipants().stream()
            .anyMatch(p -> p.getId().equals(userId)))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Channel> updateChannelName(UUID channelId, String newName) {
    Map<UUID, Channel> channelsRepository = loadData();
    Optional<Channel> channelOpt = Optional.ofNullable(channelsRepository.get(channelId))
        .map(channel -> {
          channel.updateName(newName);
          return channel;
        });

    if (channelOpt.isPresent()) {
      saveData(channelsRepository);
    }
    return channelOpt;
  }

  @Override
  public void addParticipant(UUID channelId, User user)
      throws ChannelNotFoundException, ParticipantAlreadyExistsException {
    Map<UUID, Channel> channelsRepository = loadData();
    Channel channel = channelsRepository.get(channelId);
    if (channel == null) {
      throw new ChannelNotFoundException(channelId);
    }

    if (!channel.addParticipant(user)) {
      throw new ParticipantAlreadyExistsException(user.getId(), channelId);
    }
    saveData(channelsRepository);
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId)
      throws ChannelNotFoundException, ParticipantNotFoundException {
    Map<UUID, Channel> channelsRepository = loadData();
    Channel channel = channelsRepository.get(channelId);
    if (channel == null) {
      throw new ChannelNotFoundException(channelId);
    }

    if (!channel.removeParticipant(userId)) {
      throw new ParticipantNotFoundException(userId, channelId);
    }
    saveData(channelsRepository);
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    Map<UUID, Channel> channelsRepository = loadData();
    Optional<Channel> channel = Optional.ofNullable(channelsRepository.remove(channelId));
    if (channel.isPresent()) {
      saveData(channelsRepository);
    }
    return channel;
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, Channel> loadData() {
    Map<UUID, Channel> channelsRepository = new HashMap<>();
    File file = new File(FILE_PATH);

    if (!file.exists()) {
      createData();
      return channelsRepository;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      Object obj = ois.readObject();
      if (obj instanceof Map) {
        channelsRepository.putAll((Map<UUID, Channel>) obj);
      }
    } catch (FileNotFoundException e) {
      // 파일이 없을 경우 초기 상태 유지
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return channelsRepository;
  }

  private void createData() {
    File file = new File(FILE_PATH);
    File parentDir = file.getParentFile();

    if (parentDir != null && !parentDir.exists()) {
      if (!parentDir.mkdirs()) {
        System.err.println("폴더 생성 실패: " + parentDir.getAbsolutePath());
        return;
      }
    }

    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveData(Map<UUID, Channel> channelsRepository) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(channelsRepository);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException(UUID channelId) {
      super("채널을 찾을 수 없음: " + channelId);
    }
  }

  public class ParticipantAlreadyExistsException extends RuntimeException {

    public ParticipantAlreadyExistsException(UUID userId, UUID channelId) {
      super("이미 채널에 참여 중인 사용자입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
  }

  public class ParticipantNotFoundException extends RuntimeException {

    public ParticipantNotFoundException(UUID userId, UUID channelId) {
      super("채널에서 사용자를 찾을 수 없습니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
  }
}