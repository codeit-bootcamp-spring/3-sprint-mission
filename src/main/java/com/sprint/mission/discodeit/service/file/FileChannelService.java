package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

  private final ChannelRepository channelRepository;

  public FileChannelService(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
  }

  public static FileChannelService from(String filePath) {
    return new FileChannelService(FileChannelRepository.from(filePath));
  }

  public static FileChannelService createDefault() {
    return new FileChannelService(FileChannelRepository.createDefault());
  }

  @Override
  public Channel createChannel(User creator, String name) {
    Channel channel = Channel.create(creator, name);
    return channelRepository.save(channel);
  }

  @Override
  public Optional<Channel> getChannelById(UUID id) {
    return channelRepository.findById(id);
  }

  @Override
  public List<Channel> searchChannels(UUID creatorId, String name) {
    List<Channel> allChannels = channelRepository.findAll();
    return allChannels.stream()
        .filter(channel -> creatorId == null || channel.getCreator().getId().equals(creatorId))
        .filter(channel -> name == null || channel.getName().contains(name))
        .toList();
  }

  @Override
  public List<Channel> getUserChannels(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.getParticipants().stream()
            .anyMatch(user -> user.getId().equals(userId)))
        .toList();
  }

  @Override
  public Optional<Channel> updateChannelName(UUID channelId, String name) {
    return channelRepository.findById(channelId)
        .map(channel -> {
          channel.updateName(name);
          return channelRepository.save(channel);
        });
  }

  @Override
  public void addParticipant(UUID channelId, User user) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    if (channel.addParticipant(user)) {
      channelRepository.save(channel);
    } else {
      throw new ParticipantAlreadyExistsException(user.getId(), channelId);
    }
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    boolean removed = channel.removeParticipant(userId);
    if (!removed) {
      throw new ParticipantNotFoundException(userId, channelId);
    }

    channelRepository.save(channel);
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    Optional<Channel> channel = channelRepository.findById(channelId);
    channel.ifPresent(c -> channelRepository.deleteById(channelId));
    return channel;
  }

  public static class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException(UUID channelId) {
      super("채널을 찾을 수 없음: " + channelId);
    }
  }

  public static class ParticipantAlreadyExistsException extends RuntimeException {

    public ParticipantAlreadyExistsException(UUID userId, UUID channelId) {
      super("이미 채널에 참여 중인 사용자입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
  }

  public static class ParticipantNotFoundException extends RuntimeException {

    public ParticipantNotFoundException(UUID userId, UUID channelId) {
      super("채널에서 사용자를 찾을 수 없습니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
  }
}
