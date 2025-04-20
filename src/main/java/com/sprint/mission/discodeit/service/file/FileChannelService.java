package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

// 서비스 계층은 비즈니스 로직만 담당하고, 저장/조회는 ChannelRepository에 위임
public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository = new FileChannelRepository();

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
    public Channel update(UUID id, String newName) {
        Channel channel = channelRepository.findById(id);
        channel.setName(newName);
        channel.updateUpdatedAt();
        channelRepository.update(channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        channelRepository.delete(id);
    }
}
