package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;


    // 리펙토링


    @Override
    public Channel create(PrivateChannelCreateRequest privateChannelCreateRequest) {
        // PRIVATE CHANNEL 생성
        Channel privateChannel = new Channel(
                ChannelType.PRIVATE,
                // name 및 description 속성 생략
                null,
                null
        );
        channelRepository.save(privateChannel);

        // 참여한 User 별 활동상태 여부 받기
        for (UUID userId : privateChannelCreateRequest.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

            ReadStatus readStatus = new ReadStatus(
                    user.getUserId(),
                    privateChannel.getChannelId(),
                    // 가장 작은 시간 값 : LastReadAt
                    Instant.MIN

            );
            readStatusRepository.save(readStatus);
        }

        return privateChannel;
    }

    @Override
    public Channel create(PublicChannelCreateRequest publicChannelCreateRequest) {


        // PUBLIC CHANNEL 생성
        Channel publicChannel = new Channel(
                ChannelType.PUBLIC,
                publicChannelCreateRequest.getChannelName(),
                publicChannelCreateRequest.getDescription()
        );
        channelRepository.save(publicChannel);

        return publicChannel;
    }

    @Override
    public ChannelDTO find(UUID id) {
        // 유효성
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

        // 모든 메세지 중 최근의 메세지 시간
        Instant lastestMessageAt = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(id))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // PRIVATE CHANNEL : 사용자 ID 포함
        List<UUID> participantIds = new ArrayList<>();
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(readStatus -> readStatus.getChannelId().equals(id))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toList());
        }

        return new ChannelDTO(
                channel.getChannelId(),
                channel.getChannelName(),
                channel.getChannelType(),
                channel.getDescription(),
                // 최근 시간
                lastestMessageAt,
                // PRIVATE : 사용자 ID 포함  |  PUBLIC : 공란
                participantIds
        );
    }

    @Override
    public List<ChannelDTO> findAllByUserId(UUID userId) {
        // 모든 채널 조회
        List<Channel> allChannels = channelRepository.findAll();

        // PRIVATE CHANNEL : 참여 UID 포함( 중복 X )
        Set<UUID> privateChannelIdsForUser = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        // 메세지 목록 조회
        List<Message> allMessages = messageRepository.findAll();

        // 반환할거 담을 변수 생성
        List<ChannelDTO> channelFindResponses = new ArrayList<>();

        for (Channel channel : allChannels) {
            boolean isPublic = channel.getChannelType() == ChannelType.PUBLIC;

            // PUBLIC : 전체 포함 || PRIVATE : 참여자만
            if (isPublic || privateChannelIdsForUser.contains(channel.getChannelId())) {
                // 최신 메세지
                Instant lastestMessageAt = allMessages.stream()
                        .filter(message -> message.getChannelId().equals(channel.getChannelId()))
                        .map(Message::getCreatedAt)
                        .max(Instant::compareTo)
                        .orElse(null);

                // PRIVATE
                List<UUID> participantIds = new ArrayList<>();
                if (!isPublic) {
                    participantIds = readStatusRepository.findAll().stream()
                            .filter(readStatus -> readStatus.getChannelId().equals(channel.getChannelId()))
                            .map(ReadStatus::getUserId)
                            .collect(Collectors.toList());
                }

                // 반환값 저장
                channelFindResponses.add(new ChannelDTO(
                        channel.getChannelId(),
                        channel.getChannelName(),
                        channel.getChannelType(),
                        channel.getDescription(),
                        lastestMessageAt,
                        participantIds
                ));
            }
        }

        return channelFindResponses;
    }

    @Override
    public Channel update(UUID channelId, ChannelUpdateRequest channelUpdateRequest) {
        // 유효성
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        // PRIVATE CHANNEL : 수정 금지
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE CHANNEL은 수정할 수 없습니다");
        }

        // PUBLIC CHANNEL : 수정 적용 가능
        channel.update(
                channelUpdateRequest.getChannelName(),
                channelUpdateRequest.getDescription()
        );

        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        // cascade ( Message && ReadStatus )

        // Message delete
        List<Message> messages = messageRepository.findAll().stream()
                        .filter(message -> message.getChannelId().equals(channelId))
                                .collect(Collectors.toList());
        // 해당하는 메세지 찾기
        for (Message message : messages) {
            messageRepository.deleteById(message.getMessageId());
        }

        // ReadStatus delete
        List<ReadStatus> readStatuses = readStatusRepository.findAll().stream()
                        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                                .collect(Collectors.toList());
        // 해당 정보 찾기
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getReadId());
        }

        // 채널 삭제
        channelRepository.deleteById(channelId);
    }
}

