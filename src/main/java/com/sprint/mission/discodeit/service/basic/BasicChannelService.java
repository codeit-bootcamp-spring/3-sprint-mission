package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
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
    public ChannelCreateResponse createChannel(PrivateChannelCreateRequest request) {
        List<User> users = Optional.ofNullable(request.users())
                .orElseThrow(() -> new IllegalArgumentException("no users in request") );
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPrivateChannelByName();

        // readStatus 생성
        readStatusRepository.createByUserId(userIds, channel.getId());

        return new ChannelCreateResponse(channel.getId(), channel.getType(), channel.getUpdatedAt());
    }

    @Override
    public ChannelCreateResponse createChannel(PublicChannelCreateRequest request) {
        String channelName = Optional.ofNullable(request.getChannelName())
                .orElseThrow(() -> new IllegalArgumentException("no name in request"));
        String description = Optional.ofNullable(request.getDescription())
                .orElseThrow(() -> new IllegalArgumentException("no description in request"));

        List<User> users = request.getUsers();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPublicChannelByName(channelName, description);

        // readStatus 생성
        readStatusRepository.createByUserId(userIds, channel.getId());

        return new ChannelCreateResponse(
                channel.getId(),
                channel.getType(),
                channel.getUpdatedAt(),
                channel.getName(),
                channel.getDescription()
        );
    }

    @Override
    public List<ChannelFindResponse> findAllByUserId(ChannelFindByUserIdRequest request) {
        UUID userId = Optional.ofNullable(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("no userId in request"));

        List<ChannelFindResponse> response = new ArrayList<>();
        // 유저가 참가한 방이 없을 수 있음
        List<Channel> allChannels = channelRepository.findAllChannel();
        List<Message> messageList = messageRepository.findAllMessages();

        for (Channel channel : allChannels) {
            if (channel.getType().equals(ChannelType.PUBLIC)) {
                Instant resentPublicMessageTime = messageList.stream()
                        .filter(message -> message.getChannelId().equals(channel.getId()))
                        .map(BaseEntity::getUpdatedAt)
                        .max(Instant::compareTo)
                        .orElse(null);

                response.add(new ChannelFindResponse(channel, resentPublicMessageTime));
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
                    Instant recentPrivateMessageTime = messageList.stream()
                            .filter(message -> message.getChannelId().equals(channel.getId()))
                            .map(BaseEntity::getUpdatedAt)
                            .max(Instant::compareTo)
                            .orElse(null);

                    response.add(new ChannelFindResponse(channel, recentPrivateMessageTime, userIds.stream().toList()));
                }
            }
        }
        return response;
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
    public void update(ChannelUpdateRequest request) {
        UUID channelId = Optional.ofNullable(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("no channelId in request"));

        Channel channel = Optional.ofNullable(channelRepository
                .findChannelById(channelId)).orElseThrow(() -> new RuntimeException("no channel repository"));

        if (channel.getType().equals(ChannelType.PUBLIC)) {
            Optional.ofNullable(channelId)
                    .orElseThrow(() -> new IllegalArgumentException("채널 아이디 입력 없음: BasicChannelService.update"));
            Optional.ofNullable(request.name())
                    .orElseThrow(() -> new IllegalArgumentException("이름 입력 없음: BasicChannelService.update"));

            channelRepository.updateChannel(channelId, request.name());
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: BasicChannelService.deleteChannel");

        // 하나의 객체도 삭제 실패가 없어야 하나?
        List<ReadStatus> targetReadStatuses = readStatusRepository.findReadStatusesByChannelId(channelId);
        for (ReadStatus readStatus : targetReadStatuses) {
            readStatusRepository.deleteReadStatusById(readStatus.getId()); // file: throw exception  | jcf: no exception
        }

        List<Message> targetMessages = messageRepository.findMessagesByChannelId(channelId);
        for (Message targetMessage : targetMessages) {
            messageRepository.deleteMessageById(targetMessage.getId()); // file: throw exception | jcf: no exception
        }

        channelRepository.deleteChannel(channelId); // file | jcf : throw exception
    }
}
