package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        String channelName = request.channelName();
        String password = request.password();
        UUID ownerChannelId = request.ownerChannelId();
        
        Channel channel = new Channel(ChannelType.PUBLIC,channelName, password,ownerChannelId);
        
        return channelRepository.save(channel);
    }
    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getChannelId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        return channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(PublicChannelUpdateRequest request) {
        String channelName = request.channelName();
        String password = request.password();
        UUID channelId = request.channelId();

        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        channel.updateChannelName(channelName);
        channel.updatePassword(password);

        
        return channelRepository.save(channel);

    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        messageRepository.deleteById(channel.getChannelId());
        readStatusRepository.deleteAllByChannelId(channel.getChannelId());
        channelRepository.deleteById(channelId);
    }

}
