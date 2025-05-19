package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // 생성
    UserStatus create(UserStatusCreateRequest createRequestDTO);

    // 전체 조회
    List<UserStatus> findAll();

    // 조회 (id)
    UserStatus find(UUID id);

    // 조회 (userId)
    UserStatus findByUserId(UUID userId);

    // 수정
    UserStatus update(UUID userStatusId, UserStatusUpdateRequest updateRequestDTO);

    // 수정 (userId)
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest updateRequestDTO);

    // 삭제
    void delete(UUID id);
}
