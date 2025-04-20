package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
  private final ChannelRepository channelRepository;

  public FileChannelService(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
  }
  @Override
  public Channel create(String name) {
    Channel channel = new Channel(name);
    channelRepository.save(channel);
    return channel;
  }

  @Override
  public Channel findById(UUID id) {
    return channelRepository.findById(id);
  }

  @Override
  public List<Channel> findAll() {
    return channelRepository.findAll();
  }

  @Override
  public void update(UUID id, String newName) {
    Channel channel = channelRepository.findById(id);
    if (channel != null) {
      channel.updateName(newName);
      channelRepository.save(channel);
    }
  }

  @Override
  public void delete(UUID id) {
    channelRepository.delete(id);
  }
}
