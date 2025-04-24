package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileChannelService implements ChannelService {

  private final FileChannelRepository channelRepository;

  public FileChannelService(FileChannelRepository channelRepository) {
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
  public void addParticipant(UUID channelId, User user) throws ChannelException {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.addParticipant(user);
    channelRepository.save(channel);
  }

  @Override
  public void removeParticipant(UUID channelId, UUID userId) throws ChannelException {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelException.notFound(channelId));
    channel.removeParticipant(userId);
    channelRepository.save(channel);
  }

  @Override
  public Optional<Channel> deleteChannel(UUID channelId) {
    Optional<Channel> channel = channelRepository.findById(channelId);
    channel.ifPresent(c -> channelRepository.deleteById(channelId));
    return channel;
  }
}
