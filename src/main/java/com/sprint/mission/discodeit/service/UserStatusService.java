package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // 유저 접속 상태 생성
    UserStatusDto create(UserStatusCreateRequest request);

    // 모든 유저의 접속 상태 조회
    List<UserStatusDto> findAll();

    // 유저 상태 업데이트 (상태 아이디 사용)
    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

    // 유저 상태 업데이트 (유저 아이디 사용)
    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    // 아이디로 상태 삭제
    void delete(UUID statusId);

    // 유저 상태 조회
    UserStatusDto find(UUID userStatusId);
}
