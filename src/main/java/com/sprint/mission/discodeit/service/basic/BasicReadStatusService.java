package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        log.info("읽음 상태 생성 중 - userId: {}, channelId: {}", userId, channelId);

        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new UserNotFoundException(userId));
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new ChannelNotFoundException(channelId)
            );

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
            throw new ReadStatusAlreadyExistsException(userId, channelId);
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);

        log.info("읽음 상태 생성 완료 - readStatusId: {}, userId: {}, channelId: {}",
            readStatus.getId(), userId, channelId);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .map(readStatusMapper::toDto)
            .orElseThrow(
                () -> new ReadStatusNotFoundException(readStatusId));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info("읽음 상태 수정 중 - readStatusId: {}", readStatusId);

        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(
                () -> new ReadStatusNotFoundException(readStatusId));

        readStatus.update(newLastReadAt);

        log.info("읽음 상태 수정 완료 - readStatusId: {}", readStatusId);

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        log.info("읽음 상태 삭제 시작 - readStatusId: {}", readStatusId);

        if (!readStatusRepository.existsById(readStatusId)) {
            throw new ReadStatusNotFoundException(readStatusId);
        }
        readStatusRepository.deleteById(readStatusId);

        log.info("읽음 상태 삭제 완료 - readStatusId: {}", readStatusId);
    }
}
