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
    public ResponseEntity<ChannelCreateResponse> createChannel(PublicChannelCreateRequest request) {
        String channelName = request.name();
        String description = request.description();
//        List<UUID> userIds = request.userIds().stream().map(UUID::fromString).toList();

        // channel 생성
        Channel channel = channelRepository.createPublicChannelByName(channelName, description);

        // readStatus 생성
//        readStatusRepository.createByUserId(userIds, channel.getId());

        ChannelCreateResponse channelCreateResponse = new ChannelCreateResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(channelCreateResponse);
    }

  @Override
  public ResponseEntity<ChannelCreateResponse> createChannel(PrivateChannelCreateRequest request) {
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

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(channelCreateResponse);
  }

    @Override
    public ResponseEntity<?> findAllByUserId(UUID userId) {

        List<ChannelsFindResponse> responses = new ArrayList<>();
        // 유저가 참가한 방이 없을 수 있음
        List<Channel> allChannels = channelRepository.findAllChannel();
        List<Message> messageList = messageRepository.findAllMessages();

        for (Channel channel : allChannels) {
            if (channel.getType().equals(ChannelType.PUBLIC)) {
                List<UUID> participantIds = new ArrayList<>();
                Instant lastMessageAt = messageList.stream()
                        .filter(message -> message.getChannelId().equals(channel.getId()))
                        .peek(message -> participantIds.add(message.getSenderId()))
                        .map(BaseEntity::getUpdatedAt)
                        .max(Instant::compareTo)
                        .orElse(null);

//                private final UUID id;
//                private final ChannelType type;
//                private final String name;
//                private final String description;
//                private List<UUID> participantIds;
//                private final Instant lastMessageAt;

                ChannelsFindResponse response = new ChannelsFindResponse(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription(),
                        participantIds,
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
            if (channel.getType().equals(ChannelType.PRIVATE)) {

                Set<UUID> userIds = readStatusRepository.findReadStatusesByChannelId(channel.getId()).stream()
                        .map(ReadStatus::getUserId)
                        .collect(Collectors.toSet());

                if (userIds.contains(userId)) {
                    Instant lastMessageAt = messageList.stream()
                            .filter(message -> message.getChannelId().equals(channel.getId()))
                            .map(BaseEntity::getUpdatedAt)
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
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responses);
    }

    @Override
    public ChannelFindResponse find(ChannelFindRequest request) {
        // channelId를 통해 채널을 찾아 추가 - not nullable
        // channelId를 통해 모든 메세지 리스트 생성 - nullable
        Channel channel = Optional.ofNullable(channelRepository.findChannelById(request.channelId()))
                .orElseThrow(() -> new IllegalArgumentException("no channel based on request.channelId")); // 채널
        List<Message> messageList = messageRepository.findAllMessages();

/*
        -> create에서 참여유저들이 리스트로 들어옴
        -> 채널 생성
        -> 채널과, 유저 정보로 readStatus생성
        -> find에선 channelId를 통해 채널 찾음(public
        -> channelId가 같은 메세지 조회

        -> 가장 마지막에 추가된 메세지의 시간 찾음(public)
        -> readstatus를 channelId를 통해 channelId가 같은 유저들의 id 찾음(private)
 */
        // 1. public
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            Instant resentPublicMessageTime = messageList.stream()
                    .filter(message -> message.getChannelId().equals(request.channelId()))
                    .map(BaseEntity::getUpdatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            return new ChannelFindResponse(channel, resentPublicMessageTime);
        }

        // 2. private
        if (channel.getType().equals(ChannelType.PRIVATE)) {

            Instant recentPrivateMessageTime = messageList.stream()
                    .filter(message -> message.getChannelId().equals(request.channelId()))
                    .map(BaseEntity::getUpdatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            Set<UUID> userIds = readStatusRepository.findReadStatusesByChannelId(request.channelId()).stream()
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toSet());

            return new ChannelFindResponse(channel, recentPrivateMessageTime, userIds.stream().toList());
        }
        return null;
    }

    @Override
    public ResponseEntity<?> update(UUID channelId, ChannelUpdateRequest request) {

      Channel channel = channelRepository.findChannelById(channelId);

      channelRepository.findChannelById(channelId);

      if (channel == null) {
        return ResponseEntity.status(404).body("channel with id " + channelId + "not found");
      }

      if (channel.getType().equals(ChannelType.PUBLIC)) {
        channelRepository.updateChannelName(channelId, request.newName());
        channelRepository.updateChannelDescription(channelId, request.newDescription());
      } else {
        return ResponseEntity
                .status(400)
                .body("private channel cannot be updated");
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

      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<?> deleteChannel(UUID channelId) {
      if (channelId == null) {
        return ResponseEntity.status(404).body("Channel with id " + channelId + " not found");
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
        return ResponseEntity.status(204).body("");
      }
      return ResponseEntity.status(400).body("channel not deleted for some random reason");
    }
}
