package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicChannelService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 * 2025. 4. 17.        doungukkim       null 확인 로직 추가
 */
@Primary
@Service("basicChannelService")
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public ChannelCreateResponse createChannel(PublicChannelCreateRequest request) {
        String channelName = request.name();
        String description = request.description();

        // channel 생성
        Channel channel = channelRepository.createPublicChannelByName(channelName, description);

        ChannelCreateResponse channelCreateResponse = new ChannelCreateResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
        return channelCreateResponse;
    }

    @Override
    public ChannelCreateResponse createChannel(PrivateChannelCreateRequest request) {
        List<UUID> userIds = request.participantIds().stream().map(UUID::fromString).toList();

        // channel 생성
        Channel channel = channelRepository.createPrivateChannelByName();

        // readStatus 생성
        readStatusRepository.createByUserId(userIds, channel.getId());

        ChannelCreateResponse channelCreateResponse = new ChannelCreateResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType());

        return channelCreateResponse;
    }

    @Override
    public List<ChannelsFindResponse> findAllByUserId(UUID userId) {

        List<ChannelsFindResponse> responses = new ArrayList<>();
        // 유저가 참가한 방이 없을 수 있음
        List<Channel> allChannels = channelRepository.findAllChannel();
        List<Message> messageList = messageRepository.findAllMessages();

        // 모든 방 순회
        for (Channel channel : allChannels) {
            // PUBLIC - 메세지 리스트에서 메세지의 체널아이디 확인 - 참가자 uuid 리스트 뽑아냄 - 시간만 뽑아냄 - 가장 최근 시간은 뽑아옴
            if (channel.getType().equals(ChannelType.PUBLIC)) {
                Set<UUID> participantIds = new HashSet<>();
                Instant lastMessageAt = messageList.stream()
                        .filter(message -> message.getChannelId().equals(channel.getId()))
                        .peek(message -> participantIds.add(message.getSenderId()))
                        .map(BaseUpdatableEntity::getUpdatedAt)
                        .max(Instant::compareTo)
                        .orElse(null);

                ChannelsFindResponse response = new ChannelsFindResponse(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription(),
                        participantIds.stream().toList(),
                        lastMessageAt
                );

                responses.add(response);
            }

/*
       -> 가장 마지막에 추가된 메세지의 시간 찾음(public)
       -> readstatus를 channelId를 통해 channelId가 같은 유저들의 id 조회
       ++ userIds 속에 parameter userId 있는지 확인
       ( 채널, 메세지 시간, userIds(참여자들),<= 이중에 파라미터 userId가 있어야 함 )
 */
            // PRIVATE
            if (channel.getType().equals(ChannelType.PRIVATE)) {

                Set<UUID> userIds = readStatusRepository.findReadStatusesByChannelId(channel.getId()).stream()
                        .map(ReadStatus::getUserId)
                        .collect(Collectors.toSet());

                if (userIds.contains(userId)) {
                    Instant lastMessageAt = messageList.stream()
                            .filter(message -> message.getChannelId().equals(channel.getId()))
                            .map(BaseUpdatableEntity::getUpdatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    ChannelsFindResponse response = new ChannelsFindResponse(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            userIds.stream().toList(),
                            lastMessageAt
                    );
                    responses.add(response);
                }
            }
        }
        return responses;
    }


    @Override
    public UpdateChannelResponse update(UUID channelId, ChannelUpdateRequest request) {

        Channel channel = channelRepository.findChannelById(channelId);

        channelRepository.findChannelById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("channel with id " + channelId + "not found");
        }

        if (channel.getType().equals(ChannelType.PUBLIC)) {
            channelRepository.updateChannelName(channelId, request.newName());
            channelRepository.updateChannelDescription(channelId, request.newDescription());
        } else {
            throw new NoSuchElementException("private channel cannot be updated");
        }
        channel = channelRepository.findChannelById(channelId);
        UpdateChannelResponse response = new UpdateChannelResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
        return response;
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        if (channelId == null) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        // 하나의 객체도 삭제 실패가 없어야 하나? YES
        List<ReadStatus> targetReadStatuses = readStatusRepository.findReadStatusesByChannelId(channelId);
        for (ReadStatus readStatus : targetReadStatuses) {
            readStatusRepository.deleteReadStatusById(readStatus.getId()); // throw
        }

        List<Message> targetMessages = messageRepository.findMessagesByChannelId(channelId);
        for (Message targetMessage : targetMessages) {
            messageRepository.deleteMessageById(targetMessage.getId()); // throw
        }

        boolean deleted = channelRepository.deleteChannel(channelId);
        if (deleted) {
            return true;
        }
        throw new RuntimeException("channel not deleted for some random reason");
    }

}
