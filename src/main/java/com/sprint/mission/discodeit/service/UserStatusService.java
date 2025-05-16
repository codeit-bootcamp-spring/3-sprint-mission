package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // 유저 접속 상태 생성 (요청 DTO로 파라미터 받아오고, 응답 DTO로 반환해줌)
    UserStatus createUserStatus(UserStatusCreateRequest request);

    // 유저 접속 상태 조회
    UserStatus findUserStatusById(UUID userId);

    // 모든 유저의 접속 상태 조회
    List<UserStatus> findAllStatus();

    // 상태 업데이트
    boolean updateUserStatus(UserStatusUpdateRequest request);

    // 아이디로 상태 삭제
    void deleteUserStatus(UUID statusId);

    // 유저 아이디로 상태 삭제
    void deleteUserStatusByUserId(UUID userId);
}
