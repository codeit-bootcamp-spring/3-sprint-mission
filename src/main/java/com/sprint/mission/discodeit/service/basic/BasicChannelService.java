package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;

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
      throws ChannelException {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.addParticipant(user);
    user.addChannel(channel);
    channelRepository.save(channel);
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId)
      throws ChannelException {

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.removeParticipant(userId);
    channelRepository.save(channel);
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    Optional<Channel> deleted = channelRepository.findById(channelId);
    deleted.ifPresent(channel -> channelRepository.deleteById(channelId));
    return deleted;
  }
}