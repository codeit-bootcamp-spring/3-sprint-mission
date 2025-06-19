package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
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
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.info("ReadStatus 생성 요청: userId={}, channelId={}, lastReadAt={}",
            request.userId(), request.channelId(), request.lastReadAt());

        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("ReadStatus 생성 실패: 존재하지 않는 사용자 ID={}", userId);
                return new NoSuchElementException("User with id " + userId + " does not exist");
            });

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("ReadStatus 생성 실패: 존재하지 않는 채널 ID={}", channelId);
                return new NoSuchElementException(
                    "Channel with id " + channelId + " does not exist");
            });

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
            log.error("중복 ReadStatus 생성 시도: userId={}, channelId={}", userId, channelId);
            throw new IllegalArgumentException(
                "ReadStatus with userId " + userId + " and channelId " + channelId
                    + " already exists");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);

        log.info("ReadStatus 생성 완료: id={}", readStatus.getId());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.debug("ReadStatus 조회 요청: id={}", readStatusId);

        return readStatusRepository.findById(readStatusId)
            .map(readStatusMapper::toDto)
            .orElseThrow(() -> {
                log.warn("ReadStatus 조회 실패: 존재하지 않는 ID={}", readStatusId);
                return new NoSuchElementException(
                    "ReadStatus with id " + readStatusId + " not found");
            });
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.debug("사용자 기준 ReadStatus 전체 조회: userId={}", userId);

        return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info("ReadStatus 수정 요청: id={}, newLastReadAt={}", readStatusId,
            request.newLastReadAt());

        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> {
                log.warn("ReadStatus 수정 실패: 존재하지 않는 ID={}", readStatusId);
                return new NoSuchElementException(
                    "ReadStatus with id " + readStatusId + " not found");
            });

        readStatus.update(newLastReadAt);
        log.info("ReadStatus 수정 완료: id={}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        log.info("ReadStatus 삭제 요청: id={}", readStatusId);

        if (!readStatusRepository.existsById(readStatusId)) {
            log.error("ReadStatus 삭제 실패: 존재하지 않는 ID={}", readStatusId);
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }

        readStatusRepository.deleteById(readStatusId);
        log.info("ReadStatus 삭제 완료: id={}", readStatusId);
    }
}