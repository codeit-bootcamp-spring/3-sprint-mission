package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel create(PublicChannelCreateRequest channelCreateRequest) {
        String name = channelCreateRequest.name();
        String description = channelCreateRequest.description();

        if (getByName(name) != null) {
            throw new IllegalArgumentException("[Channel] 이미 존재하는 채널명입니다. (" + name + ")");
        }

        Channel channel = Channel.of(ChannelType.PUBLIC, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest channelCreateRequest) {
        Channel channel = Channel.of(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        channelCreateRequest.participantIds().stream()
                .map(userId -> ReadStatus.of(userId, createdChannel.getId(), channel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public ChannelDto get(UUID channelId) {
        Channel channel = channelRepository.loadById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId=" + channelId + ")");
        }

        return toDto(channel);
    }

    @Override
    public List<ChannelDto> getAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.loadAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.loadAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public Channel getByName(String name) {
        return channelRepository.loadByName(name);
    }

    @Override
    public Instant getLastMessageInChannel(UUID channelId) {
        return messageRepository.loadByChannelId(channelId).stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        Channel channel = channelRepository.loadById(channelId);
        if (channel == null || channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널이거나 PRIVATE 채널입니다.");
        }

        channel.update(publicChannelUpdateRequest.name(), publicChannelUpdateRequest.description());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.loadById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (channelId=" + channelId + ")");
        }

        try {
            messageRepository.deleteByChannelId(channelId);
            readStatusRepository.deleteByChannelId(channelId);
            channelRepository.delete(channelId);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.loadByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.loadAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}
