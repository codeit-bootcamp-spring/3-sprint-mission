package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public JpaChannelResponse createChannel(PublicChannelCreateRequest request) {
        String channelName = request.name();
        String description = request.description();

        // channel 생성
        Channel channel = new Channel(channelName, description);
        channelRepository.save(channel);

        return channelMapper.toDto(channel);
    }

    @Override
    public JpaChannelResponse createChannel(PrivateChannelCreateRequest request) {
        List<UUID> userIds = request.participantIds().stream().map(UUID::fromString).toList();
        List<User> users = userRepository.findAllById(userIds);

        // channel 생성
        Channel channel = new Channel();
        channelRepository.save(channel);

        // readStatus 생성 -> participants dto 생성
        List<JpaUserResponse> participants=new ArrayList<>();
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
            readStatuses.add(readStatus);
        }

        List<User> userList = readStatuses.stream().map(r -> r.getUser()).toList();
        for (User user : userList) {
            participants.add(userMapper.toDto(user));
        }

        return channelMapper.toDto(channel);
    }

    @Transactional(readOnly = true)
    public List<JpaChannelResponse> findAllByUserId(UUID userId) {
        if(!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }

        List<JpaChannelResponse> responses = new ArrayList<>();
        List<Channel> channels = channelRepository.findAll();
//        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);
//        List<Channel> privateChannels = channelRepository.findAllByType(ChannelType.PRIVATE);

        // 유저가 참가한 방이 없을 수 있음
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
//         모든 방 순회
        for (Channel channel : channels) {
            if (channel.getType().equals(ChannelType.PUBLIC)) {
                responses.add(channelMapper.toDto(channel));
            } else {
                if(readStatuses.contains(channel)) {
                    responses.add(channelMapper.toDto(channel));
                }
            }
        }
//        for (Channel channel : publicChannels) {
//            responses.add(channelMapper.toDto(channel));
//        }


//        for (Channel channel : privateChannels) {
//            for(ReadStatus readStatus : readStatuses) {
//                // 이때만 확인해서 추가
//                if(readStatus.getChannel().getType().equals(ChannelType.PRIVATE)) {
//                    Channel channel = readStatus.getChannel();
//                    responses.add(channelMapper.toDto(channel));
//                }
//            }
//        }

        return responses;
    }


    @Override
    public JpaChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(()-> new IllegalArgumentException("channel with id " + channelId + "not found"));

        // channel update
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            channel.setName(request.newName());
            channel.setDescription(request.newDescription());
        } else {
            throw new NoSuchElementException("private channel cannot be updated");
        }

//        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(channel);
//        List<User> users = readStatuses.stream().map(ReadStatus::getUser).collect(Collectors.toList());
//        List<Message> messages = messageRepository.findAllByChannelId(channelId);
//
//
//        return modifyResponse(users, channel, messages);
        return channelMapper.toDto(channel);
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
//
//    private static boolean isOnline(UserStatus userStatus) {
//        Instant now = Instant.now();
//        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
//    }
//
//    private static JpaChannelResponse modifyResponse(List<User> users, Channel channel, List<Message> messagees) {
//        Instant lastMessageAt = null;
//        if (!messagees.isEmpty()) {
//            lastMessageAt = messagees.get(0).getCreatedAt();
//            for (Message message : messagees) {
//                if(message.getCreatedAt().isAfter(lastMessageAt)) {
//                    lastMessageAt = message.getUpdatedAt();
//                }
//            }
//        }
//
//        List<JpaUserResponse> participants = new ArrayList<>();
//        for (User user : users) {
//            BinaryContent profile = user.getProfile();
//            JpaBinaryContentResponse profileDto = null;
//            if(profile != null) {
//                profileDto = new JpaBinaryContentResponse(
//                        profile.getId(),
//                        profile.getFileName(),
//                        profile.getSize(),
//                        profile.getContentType()
//                );
//            }
//            JpaUserResponse userDto = new JpaUserResponse(
//                    user.getId(),
//                    user.getUsername(),
//                    user.getEmail(),
//                    profileDto,
//                    isOnline(user.getStatus())
//            );
//            participants.add(userDto);
//        }
//
//        JpaChannelResponse channelCreateResponse = new JpaChannelResponse(
//                channel.getId(),
//                channel.getType(),
//                channel.getName(),
//                channel.getDescription(),
//                participants,
//                lastMessageAt
//        );
//        return channelCreateResponse;
//    }

}
