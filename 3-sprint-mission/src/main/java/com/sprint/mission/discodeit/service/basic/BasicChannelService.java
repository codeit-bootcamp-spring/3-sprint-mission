package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    // Private 채널 생성
    @Override
    public Channel create(PrivateChannelCreateRequest channelCreateDTO) {
        List<UUID> participantIds = channelCreateDTO.participantIds();

        System.out.println("participantIds = " + participantIds);

        Channel channel =
                Channel.builder()
                .name(null)
                .description(null)
                .type(ChannelType.PRIVATE)
                .build();

        channelRepository.save(channel);

        // 참여자들의 메시지 수신 정보 생성
        participantIds.stream()
                .map(userId ->
                        ReadStatus.builder()
                        .userId(userId)
                        .channelId(channel.getId())
                        .lastReadAt(Instant.now())
                        .build())
                .forEach(readStatusRepository::save);

        return channel;
    }

    // Public 채널 생성
    @Override
    public Channel create(PublicChannelCreateRequest channelCreateDTO) {
        String name = channelCreateDTO.name();
        String description = channelCreateDTO.description();

        Channel channel =
                Channel.builder()
                .name(name)
                .description(description)
                .type(ChannelType.PUBLIC)
                .build();

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public ChannelDTO find(UUID id){

        return channelRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
    }

    @Override
    public List<ChannelDTO> findByName(String name) {

        return channelRepository.findByName(name).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<ChannelDTO> findAll() {

        return channelRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<ChannelDTO> findAllByUserId(UUID userId) {
        List<UUID> entryIds =
                readStatusRepository.findAllByUserId(userId).stream()
                        .map(ReadStatus::getChannelId)
                        .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                        || entryIds.contains(channel.getId())
                )
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Channel update(UUID id, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private 채널은 수정할 수 없습니다.");
        }

        channel.update(newName, newDescription);
        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 채팅방이 존재하지 않습니다."));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(id);
    }

    public ChannelDTO toDTO(Channel channel) {
        Instant lastMessageAt =
                messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> entryIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(entryIds::add);
        }

        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .type(channel.getType())
                .participantIds(entryIds)
                .lastMessageAt(lastMessageAt)
                .build();
    }
}
