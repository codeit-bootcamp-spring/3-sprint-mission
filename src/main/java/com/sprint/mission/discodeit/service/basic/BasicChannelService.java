package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        if (channelRepository == null) {
            throw new NullPointerException("channelRepository is null");
        }
        this.channelRepository = channelRepository;
    }

    @Override
    public void createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        if (channel.getChannelName() == null || channel.getChannelName().isBlank()) {
            throw new IllegalArgumentException("ChannelName cannot be null or blank");
        }

        channelRepository.save(channel);
    }


    @Override
    public Channel readChannel(UUID id) {
        return channelRepository.loadById(id);
    }

    @Override
    public List<Channel> readChannelByName(String name) {
        return channelRepository.loadByName(name);
    }

    @Override
    public List<Channel> readChannelByType(String type) {
        return channelRepository.loadByType(type);
    }

    @Override
    public List<Channel> readAllChannels() {
        return channelRepository.loadAll();
    }

    @Override
    public Channel updateChannel(UUID id, Channel channel) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
        if (channel.getChannelName() == null || channel.getChannelName().isBlank()) {
            throw new IllegalArgumentException("ChannelName cannot be null or blank");
        }

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public boolean deleteChannel(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        List<Channel> channels = channelRepository.loadAll();
        boolean removed = channels.removeIf(channel -> channel.getChannelId().equals(id));
        if (removed) {
            // 삭제 완료 시
            channelRepository.saveAll(channels);
            return true;
        }
        return false;
    }
}
