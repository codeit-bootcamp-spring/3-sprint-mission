package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    // 상태 저장 또는 갱신
    UserStatus save(UserStatus userStatus);

    // 특정 사용자 상태 조회
    UserStatus findByUserId(UUID userId);

    // 전체 사용자 상태 조회
    List<UserStatus> findAll();

    // 특정 사용자 상태 삭제
    void deleteByUserId(UUID userId);
}