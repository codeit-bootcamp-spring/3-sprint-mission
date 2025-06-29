package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.debug("public 채널 생성 로직 시작 - name: {}, description: {}", request.name(),
            request.description());

        String name = request.name();
        String description = request.description();

        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel newChannel = channelRepository.save(channel);
        return channelMapper.toDto(newChannel);
    }

    @Override
    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("private 채널 생성 로직 시작 - 참가자 수: {}", request.participantIds().size());

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        for (UUID userId : request.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("private 채널 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        Map.of("userId", userId)
                    );
                });
            ReadStatus readStatus = new ReadStatus(user, createdChannel,
                createdChannel.getCreatedAt());
            readStatusRepository.save(readStatus);
        }

        Channel newChannel = channelRepository.save(channel);
        return channelMapper.toDto(newChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("채널 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                return new DiscodeitException(
                    ErrorCode.USER_NOT_FOUND,
                    Map.of("userId", userId)
                );
            });

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

        return channelRepository.findAll().stream()
            .filter(channel -> channel.isPublic()
                || mySubscribedChannelIds.contains(channel.getId())
            )
            .map(channelMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("채널 수정 로직 시작 - channelId: {}", channelId);
        String newName = request.newName();
        String newDescription = request.newDescription();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("채널 수정 실패 - 존재하지 않는 채널 ID: {}", channelId);
                return new DiscodeitException(
                    ErrorCode.CHANNEL_NOT_FOUND,
                    Map.of("channelId", channelId)
                );
            });

        if (channel.isPrivate()) {
            log.warn("private 채널 수정 시도 차단 - channelId: {}", channelId);
            throw new DiscodeitException(
                ErrorCode.PRIVATE_CHANNEL_UPDATE_FORBIDDEN,
                Map.of("channelId", channelId)
            );
        }

        channel.update(newName, newDescription);
        Channel updatedChannel = channelRepository.save(channel);
        return channelMapper.toDto(updatedChannel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        log.debug("채널 삭제 로직 시작 - channelId: {}", channelId);
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error("채널 삭제 실패 - 존재하지 않는 채널 ID: {}", channelId);
                return new DiscodeitException(
                    ErrorCode.CHANNEL_NOT_FOUND,
                    Map.of("channelId", channelId)
                );
            });

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }
}