package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public ReadStatusCreateResponse create(ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusRepository.createByUserId(
                Optional.ofNullable(request.userId()).orElseThrow(() -> new RuntimeException("truoble with userId: BasicReadStatusService.create")),
                Optional.ofNullable(request.channelId()).orElseThrow(() -> new RuntimeException("truoble with channelId: BasicReadStatusService.create"))
        );
        return new ReadStatusCreateResponse(readStatus.getId(), readStatus.getUserId(), readStatus.getChannelId());
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "readStatusId param is null");
      return  Optional.ofNullable(readStatusRepository.findById(readStatusId)).orElseThrow(() -> new RuntimeException("no read status to find"));

    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId 없음: BasicReadStatusService.findAllByUserId");
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(
                Optional.ofNullable(request.readStatusId()).orElseThrow(() -> new RuntimeException("No readStatusId")),
                Optional.ofNullable(request.newTime()).orElseThrow(() -> new RuntimeException("No time in request"))
        );
    }

    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readstatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
