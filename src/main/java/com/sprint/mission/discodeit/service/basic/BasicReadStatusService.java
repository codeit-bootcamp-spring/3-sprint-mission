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

//            create
//  [ ] DTO를 활용해 파라미터를 그룹화합니다.
//            [ ] 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
//  [ ] 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        return readStatusRepository.createByUserId(
                Optional.ofNullable(request.getUserId()).orElseThrow(() -> new RuntimeException("truoble with userId: BasicReadStatusService.create")),
                Optional.ofNullable(request.getChannelId()).orElseThrow(() -> new RuntimeException("truoble with channelId: BasicReadStatusService.create"))
        );
    }

//            find
//  [ ] id로 조회합니다.
    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "readStatusId param is null");
        return Optional.ofNullable(readStatusRepository.findById(readStatusId));
    }


//            findAllByUserId
//  [ ] userId를 조건으로 조회합니다.
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId 없음: BasicReadStatusService.findAllByUserId");
        return readStatusRepository.findAllByUserId(userId);
    }

//            update
//  [ ] DTO를 활용해 파라미터를 그룹화합니다.
//        수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    @Override
    public void update(ReadStatusUpdateRequest request) {
        readStatusRepository.updateUpdatedTime(
                Optional.ofNullable(request.getReadStatusId()).orElseThrow(() -> new RuntimeException("No readStatusId")),
                Optional.ofNullable(request.getNewTime()).orElseThrow(() -> new RuntimeException("No time in request"))
        );
    }

//            delete
//  [ ] id로 삭제합니다.

    // working..
    @Override
    public void delete(UUID readStatusId) {
        Objects.requireNonNull(readStatusId, "no readstatusId to delete");
        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
