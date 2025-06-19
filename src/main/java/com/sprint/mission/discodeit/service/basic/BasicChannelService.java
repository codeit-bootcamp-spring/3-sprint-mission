package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.info("공개 채널 생성 요청: name={}, description={}", request.name(), request.description());

        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        channelRepository.save(channel);

        log.info("공개 채널 생성 완료: id={}", channel.getId());
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info("비공개 채널 생성 요청: 참여자 수={}", request.participantIds().size());

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds())
            .stream()
            .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
            .toList();
        readStatusRepository.saveAll(readStatuses);

        log.info("비공개 채널 생성 완료: id={}, 참여자 수={}", channel.getId(), readStatuses.size());
        return channelMapper.toDto(channel);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.debug("채널 조회 요청: id={}", channelId);

        return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> {
                log.warn("채널 조회 실패: 존재하지 않는 ID={}", channelId);
                return new NoSuchElementException("Channel with id " + channelId + " not found");
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.debug("사용자 채널 조회 요청: userId={}", userId);

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
            .stream()
            .map(channelMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.info("채널 수정 요청: id={}, newName={}, newDescription={}", channelId, request.newName(),
            request.newDescription());

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("채널 수정 실패: 존재하지 않는 ID={}", channelId);
                return new NoSuchElementException("Channel with id " + channelId + " not found");
            });

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 채널 수정 시도 차단: id={}", channelId);
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(request.newName(), request.newDescription());
        log.info("채널 수정 완료: id={}", channelId);
        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        log.info("채널 삭제 요청: id={}", channelId);

        if (!channelRepository.existsById(channelId)) {
            log.error("삭제 실패: 존재하지 않는 채널 id={}", channelId);
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);

        log.info("채널 삭제 완료: id={}", channelId);
    }
}