package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.advanced.UserMapper;
import com.sprint.mission.discodeit.mapper.advanced.ChannelMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.unit.ChannelService;
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
        Set<UUID> userIds = request.participantIds();
        List<User> users = userRepository.findAllById(userIds);

        //0 + 새로운 익셉션 만드는것도 생각
        if (users.size() < 2) {
            throw new UserNotFoundException(Map.of("users", "not enough users in private channel"));
        }

        // channel 생성
        Channel channel = new Channel();
        channelRepository.save(channel);

        // readStatus 생성 -> participants dto 생성
        List<JpaUserResponse> participants = new ArrayList<>();
        List<ReadStatus> readStatuses = new ArrayList<>();

        //0테스트코드 작성 끝나면 리팩토링: readStatusRepository.saveAll(readStatuses);
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

    @Override
    public List<JpaChannelResponse> findAllByUserId(UUID userId) {
        //0+유저 정보가 없을경우 exception을 반환하는게 더 적합할 수 있음
        if(!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }

        List<JpaChannelResponse> responses = new ArrayList<>();
        Set<UUID> channelIds = new HashSet<>();

        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);
        for (Channel channel : publicChannels) {
            responses.add(channelMapper.toDto(channel));
            channelIds.add(channel.getId());
        }

        // 유저가 참가한 방이 없을 수 있음
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserIdWithChannel(userId);
        List<Channel> privateChannels = readStatuses.stream().map(readStatus -> readStatus.getChannel()).toList();

        // 모든 방 순회
        for (Channel channel : privateChannels) {
            if(!channelIds.contains(channel.getId())) {
                responses.add(channelMapper.toDto(channel));
            }
        }
        return responses;
    }

    @Override
    public JpaChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId.toString())));

        // channel update
        if (channel.getType().equals(ChannelType.PUBLIC)) {
            channel.setName(request.newName());
            channel.setDescription(request.newDescription());
        } else {
            throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
        }
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void deleteChannel(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException(Map.of("channelId", channelId));
        }

        List<ReadStatus> targetReadStatuses = readStatusRepository.findAllByChannelId(channelId);

        readStatusRepository.deleteAll(targetReadStatuses);

        List<Message> targetMessages = messageRepository.findAllByChannelId(channelId);

        messageRepository.deleteAll(targetMessages);

        channelRepository.deleteById(channelId);
    }
}
