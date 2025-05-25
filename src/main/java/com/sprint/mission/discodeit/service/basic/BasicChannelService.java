package com.sprint.mission.discodeit.service.basic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

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
        UUID ownerId = request.ownerId();

        Channel channel = new Channel(ChannelType.PUBLIC, channelName, null, ownerId);
        Channel createdChannel = channelRepository.save(channel);
        createdChannel.getParticipantIds().stream()
                .map(participantUserId -> new ReadStatus(participantUserId, createdChannel.getChannelId(),
                        LocalDateTime.MIN))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        // 요청으로부터 채널 이름, 소유자 ID, 참가자 목록을 가져옴
        String channelName = request.channelName();
        UUID ownerId = request.ownerId(); // DTO 필드명에 맞춰 ownerlId 사용
        List<UUID> participantIds = request.participantIds();

        // Channel 엔티티 생성 시 DTO에서 받은 channelName, ownerId 및 password 사용
        Channel channel = new Channel(ChannelType.PRIVATE, channelName, request.password(), ownerId);
        // DTO로 받은 추가 참가자들을 채널에 추가
        if (participantIds != null) {
            for (UUID participantId : participantIds) {
                channel.addParticipant(participantId);
            }
        }

        Channel createdChannel = channelRepository.save(channel);

        // ReadStatus 생성: 채널의 최종 참가자 목록을 기준으로 생성
        // Channel 엔티티의 getParticipantIds()는 생성자 및 addParticipant를 통해 최신 상태를 가짐
        createdChannel.getParticipantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getChannelId(), LocalDateTime.MIN))
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

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        channel.updateChannelName(channelName);
        channel.updatePassword(password);

        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        channelRepository.deleteById(channelId);
    }

    @Override
    public List<ChannelDto> getChannelsByUserId(UUID userId) {
        // 1. 사용자의 ReadStatus 조회
        List<ReadStatus> userReadStatuses = readStatusRepository.findAllByUserId(userId);
        if (userReadStatuses.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. ReadStatus에서 channelId 목록 추출
        List<UUID> channelIds = userReadStatuses.stream()
                .map(ReadStatus::getChannelId)
                .distinct()
                .collect(Collectors.toList());

        // 3. channelId 목록으로 Channel 엔티티 목록 조회
        List<Channel> channels = channelRepository.findAllById(channelIds);

        // 4. Channel 엔티티를 ChannelDto로 변환
        return channels.stream().map(channel -> {
            // 4.1. 채널 참가자 ID 목록 조회
            List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getChannelId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            // 4.2. 마지막 메시지 시간 조회
            Optional<Message> lastMessageOpt = messageRepository
                    .findTopByChannelIdOrderByCreatedAtDesc(channel.getChannelId());
            LocalDateTime lastMessageAt = lastMessageOpt.map(Message::getCreatedAt).orElse(null);

            return new ChannelDto(
                    channel.getChannelId(),
                    channel.getChannelType(),
                    channel.getChannelName(),
                    channel.getPassword(),
                    participantIds,
                    lastMessageAt);
        }).collect(Collectors.toList());
    }
}
