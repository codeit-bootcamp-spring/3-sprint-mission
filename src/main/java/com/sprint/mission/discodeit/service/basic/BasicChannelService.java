package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.ChannelFindRequest;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createChannel = channelRepository.save(channel);
        request.participantIds().stream().map(userId -> new ReadStatus(userId, createChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return channelRepository.save(channel);
    }


    @Override
    public ChannelDto find(ChannelFindRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널은 없습니다."));

        Instant recentMessageAt = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getLastReadAt).max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList();

        if (channel.getType().equals(ChannelType.PUBLIC)) {
            return ChannelDto.builder()
                    .id(channel.getId())
                    .type(channel.getType())
                    .name(channel.getName())
                    .description(channel.getDescription())
                    .lastMessageAt(recentMessageAt)
                    .participantIds(null)
                    .build();
        } else {
            return ChannelDto.builder()
                    .id(channel.getId())
                    .type(channel.getType())
                    .name(channel.getName())
                    .description(channel.getDescription())
                    .lastMessageAt(recentMessageAt)
                    .participantIds(participantIds)
                    .build();
        }

    }


    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType() == ChannelType.PUBLIC
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toResponse)
                .toList();
    }

    private ChannelDto toResponse(Channel channel) {

        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);

        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .lastMessageAt(lastMessageAt)
                .participantIds(participantIds)
                .build();
    }


    @Override
    public Channel update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 채널은 없습니다."));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new RuntimeException("Private 채널은 수정할 수 없습니다. ");
        } else {
            channel.update(request.newName(), request.newDescription());
        }

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 id를 가진 채널은 없습니다.");
        }
        channelRepository.deleteById(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);

    }
}

