package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;

  public BasicChannelService(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
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
    return channelRepository.findAll().stream()
        .filter(channel -> (creatorId == null || channel.getCreator().getId().equals(creatorId))
            && (name == null || channel.getName().contains(name)))
        .collect(Collectors.toList());
  }

  @Override
  public List<Channel> getUserChannels(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> channel.getParticipants().stream()
            .anyMatch(p -> p.getId().equals(userId)))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Channel> updateChannelName(UUID channelId, String newName) {
    return channelRepository.findById(channelId)
        .map(channel -> {
          channel.updateName(newName);
          return channelRepository.save(channel);
        });
  }

  @Override
  public void addParticipant(UUID channelId, User user)
      throws ChannelNotFoundException, ParticipantAlreadyExistsException {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    if (!channel.addParticipant(user)) {
      throw new ParticipantAlreadyExistsException(user.getId(), channelId);
    }

    channelRepository.save(channel); // 상태 변경 후 저장
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId)
      throws ChannelNotFoundException, ParticipantNotFoundException {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    if (!channel.removeParticipant(userId)) {
      throw new ParticipantNotFoundException(userId, channelId);
    }

    channelRepository.save(channel); // 상태 변경 후 저장
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    Optional<Channel> deleted = channelRepository.findById(channelId);
    deleted.ifPresent(channel -> channelRepository.deleteById(channelId));
    return deleted;
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