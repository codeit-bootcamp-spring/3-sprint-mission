package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  /**
   * 사용자 상태 생성
   *
   * @param request 생성 요청 DTO
   * @return 생성된 사용자 상태
   */
  UserStatus create(UserStatusCreateRequest request);

  /**
   * ID로 사용자 상태 조회
   *
   * @param userStatusId 사용자 상태 ID
   * @return 조회된 사용자 상태
   */
  UserStatus find(UUID userStatusId);

  /**
   * userId로 사용자 상태 조회
   *
   * @param userId 사용자 ID
   * @return 조회된 사용자 상태
   */
  UserStatus findByUserId(UUID userId);

  /**
   * 전체 사용자 상태 조회
   *
   * @return 사용자 상태 목록
   */
  List<UserStatus> findAll();

  /**
   * 사용자 상태 업데이트
   *
   * @param request 업데이트 요청 DTO
   * @return 업데이트된 사용자 상태
   */
  UserStatus update(UserStatusUpdateRequest request);

  /**
   * userId로 사용자 상태 업데이트
   *
   * @param userId 사용자 ID
   * @return 업데이트된 사용자 상태
   */
  UserStatus updateByUserId(UUID userId);

  /**
   * 사용자 상태 삭제
   *
   * @param userStatusId 사용자 상태 ID
   */
  void delete(UUID userStatusId);
}

