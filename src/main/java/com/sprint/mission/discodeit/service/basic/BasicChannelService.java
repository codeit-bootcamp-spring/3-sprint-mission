package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.UUID;

public class BasicChannelService implements ChannelService {

    ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void create(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public void readAll() {
        channelRepository.read();
    }

    @Override
    public void readById(UUID id) {
        channelRepository.readById(id);
    }

    @Override
    public void update(UUID id, Channel channel) {
        channelRepository.update(id, channel);
    }

    @Override
    public void delete(Channel channel) {
        channelRepository.delete(channel);
    }
}
