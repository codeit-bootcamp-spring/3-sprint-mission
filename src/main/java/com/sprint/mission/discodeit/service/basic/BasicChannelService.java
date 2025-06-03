package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.advanced.AdvancedUserMapper;
import com.sprint.mission.discodeit.mapper.original.ChannelMapper;
//import com.sprint.mission.discodeit.mapper.original.UserMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
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
    private final AdvancedUserMapper userMapper;

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
    // quaries: 16개
    // 4 채널 2 public 2 private(-1 per channel) 전체 로직(-1)
    @Override
    public List<JpaChannelResponse> findAllByUserId(UUID userId) {
        if(!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }

        List<JpaChannelResponse> responses = new ArrayList<>();
        List<UUID> channelIds = new ArrayList<>();

        List<Channel> channels = channelRepository.findAllByType(ChannelType.PUBLIC);
        for (Channel channel : channels) {
            responses.add(channelMapper.toDto(channel));
            channelIds.add(channel.getId());
        }

        // 유저가 참가한 방이 없을 수 있음
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserIdWithChannel(userId);
        // 모든 방 순회
        for (Channel channel : readStatuses.stream().map(readStatus -> readStatus.getChannel()).toList()) {
            if (readStatuses.stream().anyMatch(status -> status.getChannel().getId().equals(channel.getId()))) {
                if(!channelIds.contains(channel.getId())) {
                    responses.add(channelMapper.toDto(channel));
                }
            }
        }
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
}
