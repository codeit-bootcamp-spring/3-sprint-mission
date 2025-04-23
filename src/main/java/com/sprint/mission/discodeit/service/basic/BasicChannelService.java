package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;


  @Override
  public Channel create(String name) {
    // Create a new channel entity
    Channel channel = new Channel(name);
    // Save channel using repository
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
  public Channel update(UUID id, String newName) {
    Channel channel = channelRepository.findById(id);
    if (channel != null) {
      channel.updateName(newName);
      channelRepository.save(channel);
    }
    return channel;
  }

  @Override
  public Channel delete(UUID id) {
    Channel channel = channelRepository.findById(id);
    if (channel != null) {
      channelRepository.delete(id);
    }
    return channel;
  }

}
