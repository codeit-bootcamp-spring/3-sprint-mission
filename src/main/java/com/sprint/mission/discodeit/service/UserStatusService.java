package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : UserStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */

public interface UserStatusService {
    //    create
//      [ ] DTO를 활용해 파라미터를 그룹화합니다.
//      [ ] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
//      [ ] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
    UserStatusCreateResponse create(UserStatusCreateRequest request);

    //    find
//      [ ] id로 조회합니다.
    UserStatus find(UUID userStatusId);

    //    findAll
//      [ ] 모든 객체를 조회합니다.
    List<UserStatus> findAll();

    //    update
//      [ ] DTO를 활용해 파라미터를 그룹화합니다.
//          수정 대상 객체의 id 파라미터, 수정할 값(시간) 파라미터
    void update(UserStatusUpdateRequest request);

    //    updateByUserId
//      [ ] userId 로 특정 User의 객체를 업데이트합니다.
    void updateByUserId(UUID userId, Instant newTime);

    //    delete
//      [ ] id로 삭제합니다.
    void delete(UUID userStatusId);
}
