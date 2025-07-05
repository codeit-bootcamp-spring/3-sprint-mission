package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

  private final Map<UUID, Channel> channelsRepository = new HashMap<>();

  @Override
  public Channel createChannel(User creator, String name) {
    Channel channel = Channel.create(creator, name);
    channelsRepository.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Optional<Channel> getChannelById(UUID id) {
    return Optional.ofNullable(channelsRepository.get(id));
  }

  @Override
  public List<Channel> searchChannels(UUID creatorId, String name) {
    return channelsRepository.values().stream()
        .filter(channel -> (creatorId == null || channel.getCreator().getId().equals(creatorId))
            && (name == null || channel.getName().contains(name)))
        .collect(Collectors.toList());
  }

  @Override
  public List<Channel> getUserChannels(UUID userId) {
    return channelsRepository.values().stream()
        .filter(channel -> channel.getParticipants().stream()
            .anyMatch(p -> p.getId().equals(userId)))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Channel> updateChannelName(UUID channelId, String newName) {
    return Optional.ofNullable(channelsRepository.get(channelId))
        .map(channel -> {
          channel.updateName(newName);
          return channel;
        });
  }

  @Override
  public void addParticipant(UUID channelId, User user)
      throws ChannelNotFoundException, ParticipantAlreadyExistsException {

    Channel channel = channelsRepository.get(channelId);
    if (channel == null) {
      throw new ChannelNotFoundException(channelId);
    }

    if (!channel.addParticipant(user)) {
      throw new ParticipantAlreadyExistsException(user.getId(), channelId);
    }
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId)
      throws ChannelNotFoundException, ParticipantNotFoundException {

    Channel channel = channelsRepository.get(channelId);
    if (channel == null) {
      throw new ChannelNotFoundException(channelId);
    }

    if (!channel.removeParticipant(userId)) {
      throw new ParticipantNotFoundException(userId, channelId);
    }
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    return Optional.ofNullable(channelsRepository.remove(channelId));
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