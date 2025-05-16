package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    // 유저가 로그인 하면 접속 시간 저장
    UserStatus saveUserStatus(UserStatus userStatus);

    // 유저 상태 조회
    Optional<UserStatus> findStatus(UUID userId);

    // 아이디로 조회
    Optional<UserStatus> findById(UUID id);

    // 상태 전체 조회
    List<UserStatus> findAllStatus();

    // 유저 상태 업데이트
    boolean updateUserStatus(UserStatus userStatus);

    // 아이디로 삭제
    void deleteById(UUID userId);

    // 유저 아이디로 삭제
    void deleteUserStatus(UUID userId);

}
