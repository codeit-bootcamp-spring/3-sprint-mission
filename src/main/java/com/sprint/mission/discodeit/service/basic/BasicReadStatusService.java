package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicReadStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicReadStatusService")
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        return readStatusRepository.createByUserId(
                Optional.ofNullable(request.getUserId()).orElseThrow(() -> new RuntimeException("truoble with userId: BasicReadStatusService.create")),
                Optional.ofNullable(request.getChannelId()).orElseThrow(() -> new RuntimeException("truoble with channelId: BasicReadStatusService.create"))
        );
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "readStatusId param is null");
        return Optional.ofNullable(readStatusRepository.findById(readStatusId));
    }


    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId 없음: BasicReadStatusService.findAllByUserId");
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(
                Optional.ofNullable(request.getReadStatusId()).orElseThrow(() -> new RuntimeException("No readStatusId")),
                Optional.ofNullable(request.getNewTime()).orElseThrow(() -> new RuntimeException("No time in request"))
        );
    }

    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readstatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
