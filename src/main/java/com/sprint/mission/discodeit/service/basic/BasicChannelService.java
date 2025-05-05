package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Channel getById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.deleteById(id);
    }
}
