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

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    public final UserRepository userRepository;
    public final ChannelRepository channelRepository;
    public final ReadStatusRepository readStatusRepository;
    public final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();
        log.info("[readStatus] 생성 요청: userId={}, channelId={}", userId, channelId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("[readStatus] 생성 실패 - 존재하지 않는 userId: userId={}", userId);
                return new UserNotFoundException(userId);
            });

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn(
                    "[readStatus] 생성 실패 - 존재하지 않는 channelId: channelId={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            log.warn("[readStatus] 생성 실패 - 해당 readStatus가 이미 존재함: userId={}, channelId={}", userId,
                channelId);
            throw new DuplicateReadStatusException(userId, channelId);
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);
        log.info("[readStatus] 생성 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}",
            readStatus.getId(), userId, channelId, lastReadAt != null ? lastReadAt : "(null)");

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.info("[readStatus] 조회 요청: id={}", readStatusId);

        return readStatusRepository.findById(readStatusId)
            .map(readStatusMapper::toDto)
            .orElseThrow(() -> {
                log.warn("[readStatus] 조회 실패 - 존재하지 않는 id: id={}", readStatusId);
                return new ReadStatusNotFoundException(readStatusId);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.info("[readStatus] 전체 조회 요청: userId={}", userId);

        List<ReadStatusDto> readStatusDtos = readStatusRepository.findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto)
            .toList();

        log.info("[readStatus] 전체 조회 응답: userId={}, 결과 개수={}", userId, readStatusDtos.size());
        return readStatusDtos;
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info("[readStatus] 업데이트 요청: id={}", readStatusId);

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> {
                log.warn("[readStatus] 업데이트 실패 - 존재하지 않는 id: id={}", readStatusId);
                return new ReadStatusNotFoundException(readStatusId);
            });

        readStatus.update(request.newLastReadAt());
        log.info("[readStatus] 업데이트 완료: readStatusId={}, requested={}, allied={}",
            readStatusId, request.newLastReadAt(), request.newLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            log.warn("[readStatus] 삭제 실패 - 존재하지 않는 id: id={}", readStatusId);
            throw new ReadStatusNotFoundException(readStatusId);
        }

        readStatusRepository.deleteById(readStatusId);
        log.info("[readStatus] 삭제 완료: id={}", readStatusId);
    }
}
