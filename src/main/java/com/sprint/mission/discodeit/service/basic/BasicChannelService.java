package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.Dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.Dto.user.UpdateUserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.service.ChannelService;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
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
@Transactional
public class BasicChannelService implements ChannelService {
    private final JpaChannelRepository channelRepository;
    private final JpaReadStatusRepository readStatusRepository;
    private final JpaUserRepository userRepository;
    private final JpaMessageRepository messageRepository;


    @Override
    public ChannelCreateResponse createChannel(PublicChannelCreateRequest request) {
        String channelName = request.name();
        String description = request.description();

        // channel 생성
        Channel channel = new Channel(channelName, description);
        channelRepository.save(channel);

        ChannelCreateResponse channelCreateResponse = new ChannelCreateResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                Collections.emptyList(),
                null
        );

        return channelCreateResponse;
    }

    @Override
    public ChannelCreateResponse createChannel(PrivateChannelCreateRequest request) {
        List<UUID> userIds = request.participantIds().stream().map(UUID::fromString).toList();
        List<User> users = userRepository.findAllById(userIds);

        // channel 생성
        Channel channel = new Channel();
        channelRepository.save(channel);
//
        // readStatus 생성 -> participants dto 생성
        List<JpaUserResponse> participants=new ArrayList<>();
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
            readStatuses.add(readStatus);
        }
        ChannelCreateResponse channelCreateResponse =
                modifyResponse(readStatuses.stream().map(r -> r.getUser()).toList(), channel, null);
        return channelCreateResponse;
    }

//    @Transactional(readOnly = true)
////    @Override
//    public List<ChannelsFindResponse> findAllByUserId2(UUID userId) {
////
//        List<ChannelsFindResponse> responses = new ArrayList<>();
//        // 유저가 참가한 방이 없을 수 있음
//        List<Channel> allChannels = channelRepository.findAll();
//        List<Message> messageList = messageRepository.findAll();
////
////        // 모든 방 순회
//        for (Channel channel : allChannels) {
//            // PUBLIC - 메세지 리스트에서 메세지의 체널아이디 확인 - 참가자 uuid 리스트 뽑아냄 - 시간만 뽑아냄 - 가장 최근 시간은 뽑아옴
//            if (channel.getType().equals(ChannelType.PUBLIC)) {
//                Set<UUID> participantIds = new HashSet<>();
//                Instant lastMessageAt = messageList.stream()
//                        .filter(message -> message.getChannel().getId().equals(channel.getId()))
//                        .peek(message -> participantIds.add(message.getAuthor().getId()))
//                        .map(BaseUpdatableEntity::getUpdatedAt)
//                        .max(Instant::compareTo)
//                        .orElse(null);
//
//                ChannelsFindResponse response = new ChannelsFindResponse(
//                        channel.getId(),
//                        channel.getType(),
//                        channel.getName(),
//                        channel.getDescription(),
//                        participantIds.stream().toList(),
//                        lastMessageAt
//                );
//
//                responses.add(response);
//            }
//
///*
//       -> 가장 마지막에 추가된 메세지의 시간 찾음(public)
//       -> readstatus를 channelId를 통해 channelId가 같은 유저들의 id 조회
//       ++ userIds 속에 parameter userId 있는지 확인
//       ( 채널, 메세지 시간, userIds(참여자들),<= 이중에 파라미터 userId가 있어야 함 )
// */
//            // PRIVATE
//            if (channel.getType().equals(ChannelType.PRIVATE)) {
//
//                Set<UUID> userIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
//                        .map(readStatus -> readStatus.getUser().getId())
//                        .collect(Collectors.toSet());
//
//
//                if (userIds.contains(userId)) {
//                    Instant lastMessageAt = messageList.stream()
//                            .filter(message -> message.getChannel().getId().equals(channel.getId()))
//                            .map(BaseUpdatableEntity::getUpdatedAt)
//                            .max(Instant::compareTo)
//                            .orElse(null);
//
//                    ChannelsFindResponse response = new ChannelsFindResponse(
//                            channel.getId(),
//                            channel.getType(),
//                            channel.getName(),
//                            channel.getDescription(),
//                            userIds.stream().toList(),
//                            lastMessageAt
//                    );
//                    responses.add(response);
//                }
//            }
//        }
//        return responses;
//    }

    @Transactional(readOnly = true)
    public List<ChannelCreateResponse> findAllByUserId(UUID userId) {
        if(!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }

        List<ChannelCreateResponse> responses = new ArrayList<>();
        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);
        // 유저가 참가한 방이 없을 수 있음
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        System.out.println("publicUserChannels = " + publicChannels.size());

        // 모든 방 순회
        for (Channel channel : publicChannels) {
            System.out.println("public");
            List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
            Set<User> usersSet = messages.stream().map(m-> m.getAuthor()).collect(Collectors.toSet());
            List<User> usersList = usersSet.stream().toList();
            responses.add(modifyResponse(usersList, channel, messages));
        }
        for(ReadStatus readStatus : readStatuses) {
            // 이때만 확인해서 추가
            if(readStatus.getChannel().getType().equals(ChannelType.PRIVATE)) {
                Channel channel = readStatus.getChannel();
                List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
                List<User> users = readStatusRepository.findAllByChannel(channel).stream().map(rs -> rs.getUser()).toList();
                responses.add(modifyResponse(users, channel, messages));
            }
        }
        return responses;
    }


    @Override
    public ChannelCreateResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(()-> new IllegalArgumentException("channel with id " + channelId + "not found"));

        // channel update
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            channel.setName(request.newName());
            channel.setDescription(request.newDescription());
        } else {
            throw new NoSuchElementException("private channel cannot be updated");
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(channel);
        List<User> users = readStatuses.stream().map(ReadStatus::getUser).collect(Collectors.toList());
        List<Message> messages = messageRepository.findAllByChannelId(channelId);


        return modifyResponse(users, channel, messages);
    }

    @Transactional
    @Override
    public boolean deleteChannel(UUID channelId) {

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("channel with id " + channelId + " not found");
        }

        List<ReadStatus> targetReadStatuses = readStatusRepository.findAllByChannelId(channelId);

        readStatusRepository.deleteAll(targetReadStatuses);

        List<Message> targetMessages = messageRepository.findAllByChannelId(channelId);

        messageRepository.deleteAll(targetMessages);

        channelRepository.deleteById(channelId);
        return true;
    }

    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }

    private static ChannelCreateResponse modifyResponse(List<User> users, Channel channel, List<Message> messagees) {
        Instant lastMessageAt = null;
        if (!messagees.isEmpty()) {
            lastMessageAt = messagees.get(0).getCreatedAt();
            for (Message message : messagees) {
                if(message.getCreatedAt().isAfter(lastMessageAt)) {
                    lastMessageAt = message.getUpdatedAt();
                }
            }
        }

        List<JpaUserResponse> participants = new ArrayList<>();
        for (User user : users) {
            BinaryContent profile = user.getProfile();
            JpaBinaryContentResponse profileDto = null;
            if(profile != null) {
                profileDto = new JpaBinaryContentResponse(
                        profile.getId(),
                        profile.getFileName(),
                        profile.getSize(),
                        profile.getContentType()
                );
            }
            JpaUserResponse userDto = new JpaUserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    profileDto,
                    isOnline(user.getStatus())
            );
            participants.add(userDto);
        }

        ChannelCreateResponse channelCreateResponse = new ChannelCreateResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participants,
                lastMessageAt
        );
        return channelCreateResponse;
    }

}
