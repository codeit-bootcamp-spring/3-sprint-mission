package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : ReadStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public interface ReadStatusService {

    ReadStatus create(ReadStatusCreateRequest request);

    Optional<ReadStatus> findById(UUID readStatusId);

    List<ReadStatus> findAllByUserId(UUID userId);

    //            update
//  [ ] DTO를 활용해 파라미터를 그룹화합니다.
//        수정 대상 객체의 id 파라미터, 수정할 값 파라미터
    void update(ReadStatusUpdateRequest request);

    //            delete
//  [ ] id로 삭제합니다.
    void delete(UUID readStatusId);
}
