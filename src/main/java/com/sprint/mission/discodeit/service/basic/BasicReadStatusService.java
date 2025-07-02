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
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicatedReadStatusException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메시지 읽음 상태(ReadStatus) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>읽음 상태 생성, 수정, 삭제, 조회 기능을 제공합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;
    private static final String SERVICE_NAME = "[ReadStatusService] ";

    /**
     * 읽음 상태를 생성합니다.
     * @param request 읽음 상태 생성 요청 정보
     * @return 생성된 읽음 상태 DTO
     */
    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();
        log.info(SERVICE_NAME + "읽음 상태 생성 시도: userId={}, channelId={}", userId, channelId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "사용자 없음: userId={}", userId);
                    return new ReadStatusNotFoundException("사용자를 찾을 수 없습니다.");
                });
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "채널 없음: channelId={}", channelId);
                    return new ReadStatusNotFoundException("채널을 찾을 수 없습니다.");
                });

        if (readStatusRepository.findAllByUserId(userId).stream()
            .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
            log.error(SERVICE_NAME + "이미 존재하는 읽음 상태: userId={}, channelId={}", userId, channelId);
            throw new DuplicatedReadStatusException("이미 존재하는 읽음 상태입니다.", userId, channelId);
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        readStatusRepository.save(readStatus);
        log.info(SERVICE_NAME + "읽음 상태 생성 성공: id={}", readStatus.getId());
        return readStatusMapper.toDto(readStatus);
    }

    /**
     * 특정 읽음 상태를 조회합니다.
     * @param readStatusId 조회할 읽음 상태 ID
     * @return 조회된 읽음 상태 DTO
     */
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.info(SERVICE_NAME + "읽음 상태 조회 시도: id={}", readStatusId);
        return readStatusRepository.findById(readStatusId)
                .map(readStatus -> {
                    log.info(SERVICE_NAME + "읽음 상태 조회 성공: id={}", readStatusId);
                    return readStatusMapper.toDto(readStatus);
                })
                .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "읽음 상태 없음: id={}", readStatusId);
                    return new ReadStatusNotFoundException("읽음 상태 정보를 찾을 수 없습니다.");
                });
    }

    /**
     * 특정 사용자의 읽음 상태 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 읽음 상태 DTO 목록
     */
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.info(SERVICE_NAME + "사용자 읽음 상태 목록 조회 시도: userId={}", userId);
        List<ReadStatusDto> result = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatusMapper.toDto(readStatus))
                .toList();
        log.info(SERVICE_NAME + "사용자 읽음 상태 목록 조회 성공: userId={}, 건수={}", userId, result.size());
        return result;
    }

    /**
     * 읽음 상태를 수정합니다.
     * @param readStatusId 수정할 읽음 상태 ID
     * @param request 읽음 상태 수정 요청 정보
     * @return 수정된 읽음 상태 DTO
     */
    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.info(SERVICE_NAME + "읽음 상태 수정 시도: id={}", readStatusId);
        Instant newLastReadAt = request.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "읽음 상태 없음: id={}", readStatusId);
                return new ReadStatusNotFoundException("읽음 상태 정보를 찾을 수 없습니다.");
            });
        readStatus.update(newLastReadAt);
        readStatusRepository.save(readStatus);
        log.info(SERVICE_NAME + "읽음 상태 수정 성공: id={}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    /**
     * 읽음 상태를 삭제합니다.
     * @param readStatusId 삭제할 읽음 상태 ID
     */
    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        log.info(SERVICE_NAME + "읽음 상태 삭제 시도: id={}", readStatusId);
        if (!readStatusRepository.existsById(readStatusId)) {
            log.error(SERVICE_NAME + "읽음 상태 없음: id={}", readStatusId);
            throw new ReadStatusNotFoundException("읽음 상태 정보를 찾을 수 없습니다.");
        }
        readStatusRepository.deleteById(readStatusId);
        log.info(SERVICE_NAME + "읽음 상태 삭제 성공: id={}", readStatusId);
    }
}
