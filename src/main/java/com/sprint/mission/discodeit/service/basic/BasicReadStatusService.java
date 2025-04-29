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
                Objects.requireNonNull(request.userId(), "trouble with userId: BasicReadStatusService.create"),
                Objects.requireNonNull(request.channelId(), "trouble with channelId: BasicReadStatusService.create")
        );
        return new ReadStatusCreateResponse(readStatus.getId(), readStatus.getUserId(), readStatus.getChannelId());
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
      return  Optional.ofNullable(readStatusRepository.findById(readStatusId)).orElseThrow(() -> new IllegalStateException("no read status to find"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return Optional.ofNullable(readStatusRepository.findAllByUserId(userId))
                .orElseThrow(()->new IllegalStateException("userId로 찾을 수 없음: BasicReadStatusService.findAllByUserId"));
    }

    @Override
    public void update(ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(
                Objects.requireNonNull(request.readStatusId(), "No readStatusId"),
                Objects.requireNonNull(request.newTime(), "no time in request")
        );
    }

    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readStatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
