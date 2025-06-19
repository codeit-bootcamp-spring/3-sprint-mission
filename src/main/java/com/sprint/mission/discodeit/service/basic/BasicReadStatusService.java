package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
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
            .orElseThrow(() -> new UserNotFoundException(userId));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
            log.error("중복 ReadStatus 생성 시도: userId={}, channelId={}", userId, channelId);
            throw new DuplicateReadStatusException(userId, channelId);
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
            .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
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
            .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

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
            throw new ReadStatusNotFoundException(readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
        log.info("ReadStatus 삭제 완료: id={}", readStatusId);
    }
}