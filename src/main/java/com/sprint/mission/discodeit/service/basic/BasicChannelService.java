package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;




    @Override
    public List<ChannelFindResponse> findAllByUserId(UUID userId) {

        List<ChannelFindResponse> response = new ArrayList<>();
        List<Channel> allChannels = Optional.ofNullable(channelRepository.findAllChannel()).orElse(Collections.emptyList());
        for (Channel channel : allChannels) {

            List<Message> messageList = messageRepository.findAllMessages();
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
       + userIds 속에 parameter userId 있는지 확인
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
    public ChannelFindResponse findChannel(UUID channelId) {
        // channelId를 통해 채널을 찾아 추가 - not nullable
        // channelId를 통해 모든 메세지 리스트 생성 - nullable
        Channel channel = Objects.requireNonNull(channelRepository.findChannelById(channelId), "맞는 채널 없음"); // 채널
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
                    .filter(message -> message.getChannelId().equals(channelId))
                    .map(BaseEntity::getUpdatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            return new ChannelFindResponse(channel, resentPublicMessageTime);
        }

        // 2. private
        if (channel.getType().equals(ChannelType.PRIVATE)) {

            Instant recentPrivateMessageTime = messageList.stream()
                    .filter(message -> message.getChannelId().equals(channelId))
                    .map(BaseEntity::getUpdatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            Set<UUID> userIds = readStatusRepository.findReadStatusesByChannelId(channelId).stream()
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toSet());


            return new ChannelFindResponse(channel, recentPrivateMessageTime, userIds.stream().toList());
        }
        return null;
    }




    // update
    // delete

    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        Objects.requireNonNull(request, "채널 입력 없음");
        List<User> users = request.getUsers();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPrivateChannelByName();
        // readstatus 생성
        System.out.println("BasicChannelService.createChannel");
        readStatusRepository.createReadStatusByUserId(userIds, channel.getId())
                .forEach(e -> System.out.println("readStatus :" + e.getId()));
        System.out.println();

        return channel;
    }

    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        Objects.requireNonNull(request, "채널 입력 없음");
        String channelName = Optional.ofNullable(request.getChannelName()).orElse("");
        String description = Optional.ofNullable(request.getDescription()).orElse("");

        List<User> users = request.getUsers();
        List<UUID> userIds = users.stream().map(User::getId).toList();

        // channel 생성
        Channel channel = channelRepository.createPublicChannelByName(channelName, description);

        // readstatus 생성
        System.out.println("BasicChannelService.createChannel");
        readStatusRepository.createReadStatusByUserId(userIds, channel.getId())
                .forEach(e -> System.out.println("readStatus :" + e.getId()));
        System.out.println();

        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "체널 아이디 입력 없음: BasicChannelService.findChannelById");
        Channel result = channelRepository.findChannelById(channelId);
        Objects.requireNonNull(result, "찾는 채널 없음: BasicChannelService.findChannelById");
        return result;
    }



    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }




    // working on it
    @Override
    public void updateChannelName(ChannelUpdateRequest request) {

        UUID channelId = request.getChannelId();
        String name = request.getName();
        Channel channel = Objects.requireNonNull(channelRepository.findChannelById(channelId), "채널 없음");
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            // 그럴수 있나? 그래선 안 되는가?
            Objects.requireNonNull(channelId, "채널 아이디 입력 없음: BasicChannelService.updateChannelName");
            Objects.requireNonNull(name, "이름 입력 없음: BasicChannelService.updateChannelName");

            channelRepository.updateChannel(channelId, name);
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "no channelId: BasicChannelService.deleteChannel");

        List<ReadStatus> targetReadStatuses = Optional.ofNullable(readStatusRepository.findReadStatusesByChannelId(channelId)).orElse(Collections.emptyList());
        for (ReadStatus readStatus : targetReadStatuses) {
            readStatusRepository.deleteReadStatusById(readStatus.getId());
        }

        List<Message> targetMessages = Optional.ofNullable(messageRepository.findMessagesByChanenlId(channelId)).orElse(Collections.emptyList());
        for (Message targetMessage : targetMessages) {
            messageRepository.deleteMessageById(targetMessage.getId());
        }

        channelRepository.deleteChannel(channelId);
    }
}
