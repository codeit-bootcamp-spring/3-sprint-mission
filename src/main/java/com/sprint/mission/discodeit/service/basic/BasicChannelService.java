package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Channel createPublic(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel saved = channelRepository.save(channel);

        for (UUID userId : request.participantIds()) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId()));
        }

        return saved;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        Instant latestTime = messageRepository.findLatestByChannelId(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findUserIdsByChannelId(channelId)
                : List.of();

        return new ChannelDto(channel, latestTime, participantIds);
    }

    @Override
    public List<ChannelDto> findAll() {
        List<Channel> allChannels = channelRepository.findAll();
        List<ChannelDto> result = new ArrayList<>();

        for (Channel channel : allChannels) {
            Instant latestTime = messageRepository.findLatestByChannelId(channel.getId())
                    .map(Message::getCreatedAt)
                    .orElse(null);

            List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                    ? readStatusRepository.findUserIdsByChannelId(channel.getId())
                    : List.of();

            result.add(new ChannelDto(channel, latestTime, participantIds));
        }

        return result;
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE Channel can't be updated");
        }

        channel.update(request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }
}
