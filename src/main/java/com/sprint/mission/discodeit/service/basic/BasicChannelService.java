package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream()
            .map(userId -> new ReadStatus(userId, createdChannel.getId(), channel.getCreatedAt()))
            .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public Optional<ChannelDto> find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(this::toDto);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannelId)
            .toList();

        return channelRepository.findAll().stream()
            .filter(channel ->
                channel.getType().equals(ChannelType.PUBLIC)
                    || mySubscribedChannelIds.contains(channel.getId())
            )
            .map(this::toDto)
            .toList();
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정이 불가능합니다.");
        }

        channel.update(newName, newDescription);
        channelRepository.update(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        List<Message> messages = messageRepository.findByChannelId(channelId);
        messageRepository.deleteAll(messages);

        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channelId);
        readStatusRepository.deleteAll(readStatuses);

        channelRepository.delete(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findByChannelId(channel.getId())
            .stream()
            .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
            .map(Message::getCreatedAt)
            .limit(1)
            .findFirst()
            .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findByChannelId(channel.getId())
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